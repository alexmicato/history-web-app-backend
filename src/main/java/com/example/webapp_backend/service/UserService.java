package com.example.webapp_backend.service;

import com.example.webapp_backend.exception.CustomException;
import com.example.webapp_backend.model.ImageEntity;
import com.example.webapp_backend.model.dto.RegistrationRequest;
import com.example.webapp_backend.model.RoleEntity;
import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.model.dto.UserDTO;
import com.example.webapp_backend.model.dto.DTOutils;
import com.example.webapp_backend.repository.RoleRepository;
import com.example.webapp_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Convert each RoleEntity to a SimpleGrantedAuthority object
        List<SimpleGrantedAuthority> authorities = user.getRoleEntities().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        // Create and return a User object (from Spring Security) with the username, password, and authorities
        return new User(user.getUsername(), user.getPassword(), authorities);
    }
    public UserEntity registerNewUser(RegistrationRequest registrationRequest) {
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new CustomException("Username is already taken.");
        }
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new CustomException("Email is already in use.");
        }

        if (!isPasswordStrong(registrationRequest.getPassword())) {
            throw new CustomException("Your password must meet the following security requirements:\n" +
                    "- At least 8 characters long\n" +
                    "- Contains at least one uppercase letter (A-Z)\n" +
                    "- Contains at least one lowercase letter (a-z)\n" +
                    "- Contains at least one digit (0-9)\n" +
                    "- Contains at least one special character from the set @$!%*?&\n");
        }

        UserEntity newUser = new UserEntity();
        newUser.setUsername(registrationRequest.getUsername());
        newUser.setEmail(registrationRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

        RoleEntity defaultRole = roleRepository.findByName("USER").orElseThrow(() -> new RuntimeException("User Role not set."));
        newUser.setRoleEntities(Collections.singleton(defaultRole));

        return userRepository.save(newUser);
    }

    private boolean isPasswordStrong(String password) {
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        return pattern.matcher(password).matches();
    }

    public boolean isOldPasswordCorrect(UserEntity user, String oldPassword) {

        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("User not found with username: " + username));
    }

    public UserEntity findByUsernameNoException(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public boolean existsByUsername(String username)
    {
        return userRepository.existsByUsername(username);
    }

    @Transactional
    public UserEntity registerAdmin(String username, String email, String password) {
        UserEntity admin = new UserEntity();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));

        // Fetch and set roles within the same transaction
        RoleEntity defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("User Role not set."));

        RoleEntity adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin Role not set."));

        Set<RoleEntity> roles = new HashSet<>();
        roles.add(defaultRole);
        roles.add(adminRole);

        admin.setRoleEntities(roles);
        return userRepository.save(admin); // Save ensures that roles are managed as part of the save operation
    }

    public List<UserDTO> findAllUsersWithRoles() {
        // Fetch all users with roles
        List<UserEntity> users = userRepository.findAllUsersWithRoles();
        // Convert users to UserDTOs
        return users.stream()
                .map(DTOutils::convertToUserDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean removeRoleFromUser(String username, String roleName) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() ->
                new NoSuchElementException("User not found with username: " + username));
        if (user != null && user.getRoleEntities().removeIf(role -> role.getName().equals(roleName))) {
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean addRoleToUser(String username, String roleName) {

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found with username: " + username));

        RoleEntity role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new NoSuchElementException("Role not found with name: " + roleName));

        if (!user.getRoleEntities().contains(role)) {
            user.getRoleEntities().add(role);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updateUsername(Long userId, String newUsername) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        user.setUsername(newUsername);
        userRepository.save(user);
        return true;
    }

    @Transactional
    public UserEntity updatePassword(UserEntity user, String newPassword)
    {
        if (user == null) {
            return null;
        }
        if(!isPasswordStrong(newPassword))
        {
            throw new IllegalArgumentException("Password does not meet the security requirements.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);

    }

    @Transactional
    public UserEntity updateProfilePic(UserEntity user, String imageUrl)
    {
        if (user == null) {
            return null;
        }
        user.setProfileImageUrl(imageUrl);
        return userRepository.save(user);
    }

}
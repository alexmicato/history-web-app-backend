package com.example.webapp_backend.unitTests.serviceTests;
import com.example.webapp_backend.exception.CustomException;
import com.example.webapp_backend.model.RoleEntity;
import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.model.dto.RegistrationRequest;
import com.example.webapp_backend.model.dto.UserDTO;
import com.example.webapp_backend.repository.RoleRepository;
import com.example.webapp_backend.repository.UserRepository;
import com.example.webapp_backend.model.dto.DTOutils;
import com.example.webapp_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserEntity userEntity;
    private RoleEntity roleEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setUsername("testUser");
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("encodedPassword");

        roleEntity = new RoleEntity();
        roleEntity.setName("USER");

        userEntity.setRoleEntities(Collections.singleton(roleEntity));
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(userEntity));

        org.springframework.security.core.userdetails.UserDetails userDetails = userService.loadUserByUsername("testUser");

        assertEquals("testUser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("USER")));
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("testUser"));
    }

    @Test
    void testRegisterNewUser_Success() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("newUser");
        request.setEmail("new@example.com");
        request.setPassword("StrongP@ssword1");

        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("StrongP@ssword1")).thenReturn("encodedPassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(roleEntity));

        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setUsername("newUser");
        newUserEntity.setEmail("new@example.com");
        newUserEntity.setPassword("encodedPassword");
        newUserEntity.setRoleEntities(Collections.singleton(roleEntity));

        when(userRepository.save(any(UserEntity.class))).thenReturn(newUserEntity);

        UserEntity registeredUser = userService.registerNewUser(request);

        assertEquals("newUser", registeredUser.getUsername());
        assertEquals("new@example.com", registeredUser.getEmail());
        assertEquals("encodedPassword", registeredUser.getPassword());
        assertTrue(registeredUser.getRoleEntities().contains(roleEntity));
    }

    @Test
    void testRegisterNewUser_UsernameTaken() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("existingUser");
        request.setEmail("new@example.com");
        request.setPassword("StrongP@ssword1");

        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        assertThrows(CustomException.class, () -> userService.registerNewUser(request));
    }

    @Test
    void testRegisterNewUser_EmailTaken() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("newUser");
        request.setEmail("existing@example.com");
        request.setPassword("StrongP@ssword1");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(CustomException.class, () -> userService.registerNewUser(request));
    }

    @Test
    void testIsPasswordStrong_ValidPassword() {
        assertTrue(userService.isPasswordStrong("StrongP@ssword1"));
    }

    @Test
    void testIsPasswordStrong_InvalidPassword() {
        assertFalse(userService.isPasswordStrong("weakpassword"));
    }

    @Test
    void testFindByUsername_UserExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(userEntity));

        UserEntity foundUser = userService.findByUsername("testUser");

        assertEquals("testUser", foundUser.getUsername());
    }

    @Test
    void testFindByUsername_UserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> userService.findByUsername("testUser"));
    }

    @Test
    void testFindByUsernameNoException_UserExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(userEntity));

        UserEntity foundUser = userService.findByUsernameNoException("testUser");

        assertEquals("testUser", foundUser.getUsername());
    }

    @Test
    void testFindByUsernameNoException_UserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        UserEntity foundUser = userService.findByUsernameNoException("testUser");

        assertNull(foundUser);
    }

    @Test
    void testUpdatePassword_Success() {
        when(passwordEncoder.encode("NewStr0ngP@ssword")).thenReturn("encodedNewPassword");
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        UserEntity updatedUser = userService.updatePassword(userEntity, "NewStr0ngP@ssword");

        assertEquals("encodedNewPassword", updatedUser.getPassword());
    }

    @Test
    void testUpdatePassword_WeakPassword() {
        assertThrows(IllegalArgumentException.class, () -> userService.updatePassword(userEntity, "weakpassword"));
    }

    @Test
    void testUpdateProfilePic_Success() {
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        UserEntity updatedUser = userService.updateProfilePic(userEntity, "http://newimageurl.com");

        assertEquals("http://newimageurl.com", updatedUser.getProfileImageUrl());
    }

    @Test
    void testUpdateProfilePic_UserNull() {
        UserEntity updatedUser = userService.updateProfilePic(null, "http://newimageurl.com");

        assertNull(updatedUser);
    }
}


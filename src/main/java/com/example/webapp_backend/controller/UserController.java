package com.example.webapp_backend.controller;


import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.model.dto.*;
import com.example.webapp_backend.service.UserService;
import org.hibernate.internal.build.AllowPrintStacktrace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<UserProfileDTO> getUserByUsername(@PathVariable String username) {
        UserEntity user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        UserProfileDTO userDTO = new UserProfileDTO(user.getUsername(), user.getProfileImageUrl());
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/user/update/profile")
    public ResponseEntity<String> updateUsername(@RequestBody UsernameUpdateDTO usernameUpdateDTO) {

        UserEntity currentUser = userService.findByUsername(usernameUpdateDTO.getCurrentUsername());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Current username not found.");
        }

        System.out.println("Current username: " + currentUser.getUsername());
        System.out.println("New username: " + usernameUpdateDTO.getNewUsername());

        if(usernameUpdateDTO.getCurrentUsername().equals(usernameUpdateDTO.getNewUsername())) {
            return ResponseEntity.badRequest().body("Username is not changed.");
        }

        // Check if the new username is already taken by someone else
        if (userService.findByUsernameNoException(usernameUpdateDTO.getNewUsername()) != null) {
            return ResponseEntity.badRequest().body("New username is already taken.");
        }

        // Attempt to update the username
        boolean updateSuccessful = userService.updateUsername(currentUser.getId(), usernameUpdateDTO.getNewUsername());
        if (updateSuccessful) {
            return ResponseEntity.ok("Username updated successfully.");
        } else {
            return ResponseEntity.internalServerError().body("Failed to update username.");
        }
    }

    @PutMapping("/user/update/password")
    public ResponseEntity<String> updateUsername(@RequestBody PasswordUpdateDTO passwordUpdateDTO)
    {
        System.out.println("Current username: " + passwordUpdateDTO.getUsername());

        UserEntity currentUser = userService.findByUsername(passwordUpdateDTO.getUsername());

        if(!userService.isOldPasswordCorrect(currentUser, passwordUpdateDTO.getOldPassword()))
        {
            System.out.println("Old password is not correct.");
            return ResponseEntity.badRequest().body("Old password is not correct.");
        }

        try {
            UserEntity updatedUser = userService.updatePassword(currentUser, passwordUpdateDTO.getNewPassword());
            return ResponseEntity.ok("Password updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/user/update/picture")
    public ResponseEntity<String> updateProfilePic(@RequestBody ProfilePicUpdateDTO profilePicUpdateDTO)
    {
        System.out.println("Current username: " + profilePicUpdateDTO.getUsername());

        UserEntity currentUser = userService.findByUsername(profilePicUpdateDTO.getUsername());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        try {
            UserEntity updatedUser = userService.updateProfilePic(currentUser, profilePicUpdateDTO.getProfilePicUrl());
            return ResponseEntity.ok("Profile picture updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}

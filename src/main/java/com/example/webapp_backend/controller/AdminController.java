package com.example.webapp_backend.controller;


import com.example.webapp_backend.model.dto.UserDTO;
import com.example.webapp_backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/users")
    public ResponseEntity<List<UserDTO>> getAllUsersWithRoles() {
        List<UserDTO> users = userService.findAllUsersWithRoles();
        return ResponseEntity.ok(users);
    }
}

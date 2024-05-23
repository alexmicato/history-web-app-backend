package com.example.webapp_backend.controller;

import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.model.data.Roles;
import com.example.webapp_backend.service.RoleService;
import com.example.webapp_backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RoleController {

    private final RoleService roleService;
    private final UserService userService;


    public RoleController(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAllRoles() {
        // Convert the Roles enum to a list of its names
        List<String> roles = Arrays.stream(Roles.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/admin/{username}/roles/remove")
    public ResponseEntity<String> removeRoleFromUser(@PathVariable String username, @RequestBody String role) {

        if ("USER".equalsIgnoreCase(role)) {
            return ResponseEntity.badRequest().body("Removing the 'USER' role is not allowed.");
        }

        boolean isRemoved = userService.removeRoleFromUser(username, role);
        if (isRemoved) {
            return ResponseEntity.ok("Role removed successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to remove role or user not found.");
        }
    }

    @PostMapping("/admin/{username}/roles/add")
    public ResponseEntity<String> addRoleToUser(@PathVariable String username, @RequestBody String role) {

        try {
            boolean isAdded = userService.addRoleToUser(username, role);
            if (isAdded) {
                return ResponseEntity.ok("Role added successfully.");
            } else {
                return ResponseEntity.badRequest().body("Failed to add role or user not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while adding the role: " + e.getMessage());
        }
    }

}

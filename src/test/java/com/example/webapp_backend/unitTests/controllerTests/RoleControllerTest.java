package com.example.webapp_backend.unitTests.controllerTests;
import com.example.webapp_backend.controller.RoleController;
import com.example.webapp_backend.model.data.Roles;
import com.example.webapp_backend.service.RoleService;
import com.example.webapp_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {
    @Mock
    private RoleService roleService;

    @Mock
    private UserService userService;

    @InjectMocks
    private RoleController roleController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roleController).build();
    }

    @Test
    void getAllRoles() throws Exception {
        List<String> roles = Arrays.stream(Roles.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        mockMvc.perform(get("/roles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"" + String.join("\",\"", roles) + "\"]"));
    }

    @Test
    void removeRoleFromUser_NotAllowed() throws Exception {
        String username = "john";
        mockMvc.perform(post("/admin/{username}/roles/remove", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("USER"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Removing the 'USER' role is not allowed."));
    }

    @Test
    void removeRoleFromUser_Success() throws Exception {
        String username = "john";
        when(userService.removeRoleFromUser(username, "ADMIN")).thenReturn(true);
        mockMvc.perform(post("/admin/{username}/roles/remove", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("ADMIN"))
                .andExpect(status().isOk())
                .andExpect(content().string("Role removed successfully."));
    }

    @Test
    void removeRoleFromUser_Failure() throws Exception {
        String username = "john";
        when(userService.removeRoleFromUser(username, "ADMIN")).thenReturn(false);

        mockMvc.perform(post("/admin/{username}/roles/remove", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("ADMIN"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed to remove role or user not found."));
    }

    @Test
    void addRoleToUser_Success() throws Exception {
        String username = "john";
        when(userService.addRoleToUser(username, "ADMIN")).thenReturn(true);

        mockMvc.perform(post("/admin/{username}/roles/add", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("ADMIN"))
                .andExpect(status().isOk())
                .andExpect(content().string("Role added successfully."));
    }

    @Test
    void addRoleToUser_Failure() throws Exception {
        String username = "john";
        when(userService.addRoleToUser(username, "ADMIN")).thenReturn(false);

        mockMvc.perform(post("/admin/{username}/roles/add", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("ADMIN"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed to add role or user not found."));
    }

    @Test
    void addRoleToUser_ErrorHandling() throws Exception {
        String username = "john";
        when(userService.addRoleToUser(username, "ADMIN")).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/admin/{username}/roles/add", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("ADMIN"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred while adding the role: Database error"));
    }
}
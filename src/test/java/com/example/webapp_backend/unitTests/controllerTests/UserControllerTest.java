package com.example.webapp_backend.unitTests.controllerTests;

import com.example.webapp_backend.controller.UserController;
import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.model.dto.*;
import com.example.webapp_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("testUser");
        userEntity.setProfileImageUrl("http://example.com/profile.jpg");
    }

    @Test
    void testGetUserByUsername_UserExists() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(userEntity);

        mockMvc.perform(get("/users/testUser"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username", is("testUser")))
                .andExpect(jsonPath("$.profileImageUrl", is("http://example.com/profile.jpg")));
    }

    @Test
    void testGetUserByUsername_UserNotFound() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(null);

        mockMvc.perform(get("/users/testUser"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUsername_Success() throws Exception {
        UsernameUpdateDTO updateDTO = new UsernameUpdateDTO("testUser", "newUser");
        UserEntity updatedUserEntity = new UserEntity();
        updatedUserEntity.setId(1L);
        updatedUserEntity.setUsername("newUser");

        when(userService.findByUsername("testUser")).thenReturn(userEntity);
        when(userService.findByUsernameNoException("newUser")).thenReturn(null);
        when(userService.updateUsername(1L, "newUser")).thenReturn(true);

        mockMvc.perform(put("/user/update/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentUsername\": \"testUser\", \"newUsername\": \"newUser\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Username updated successfully."));
    }

    @Test
    void testUpdateUsername_CurrentUsernameNotFound() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(null);

        mockMvc.perform(put("/user/update/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentUsername\": \"testUser\", \"newUsername\": \"newUser\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Current username not found."));
    }

    @Test
    void testUpdateUsername_NewUsernameAlreadyTaken() throws Exception {
        UsernameUpdateDTO updateDTO = new UsernameUpdateDTO("testUser", "newUser");

        when(userService.findByUsername("testUser")).thenReturn(userEntity);
        when(userService.findByUsernameNoException("newUser")).thenReturn(new UserEntity());

        mockMvc.perform(put("/user/update/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentUsername\": \"testUser\", \"newUsername\": \"newUser\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("New username is already taken."));
    }

    @Test
    void testUpdatePassword_Success() throws Exception {
        PasswordUpdateDTO updateDTO = new PasswordUpdateDTO("testUser", "oldPassword", "NewStr0ngP@ssword");

        when(userService.findByUsername("testUser")).thenReturn(userEntity);
        when(userService.isOldPasswordCorrect(userEntity, "oldPassword")).thenReturn(true);
        when(userService.updatePassword(userEntity, "NewStr0ngP@ssword")).thenReturn(userEntity);

        mockMvc.perform(put("/user/update/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testUser\", \"oldPassword\": \"oldPassword\", \"newPassword\": \"NewStr0ngP@ssword\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password updated successfully"));
    }

    @Test
    void testUpdatePassword_OldPasswordIncorrect() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(userEntity);
        when(userService.isOldPasswordCorrect(userEntity, "oldPassword")).thenReturn(false);

        mockMvc.perform(put("/user/update/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testUser\", \"oldPassword\": \"oldPassword\", \"newPassword\": \"NewStr0ngP@ssword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Old password is not correct."));
    }

    @Test
    void testUpdateProfilePic_Success() throws Exception {
        ProfilePicUpdateDTO updateDTO = new ProfilePicUpdateDTO("testUser", "http://newimageurl.com");

        when(userService.findByUsername("testUser")).thenReturn(userEntity);
        when(userService.updateProfilePic(userEntity, "http://newimageurl.com")).thenReturn(userEntity);

        mockMvc.perform(put("/user/update/picture")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testUser\", \"profilePicUrl\": \"http://newimageurl.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Profile picture updated successfully"));
    }

    @Test
    void testUpdateProfilePic_UserNotFound() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(null);

        mockMvc.perform(put("/user/update/picture")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testUser\", \"profilePicUrl\": \"http://newimageurl.com\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found."));
    }
}

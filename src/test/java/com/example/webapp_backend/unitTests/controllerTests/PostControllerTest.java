package com.example.webapp_backend.unitTests.controllerTests;

import com.example.webapp_backend.controller.PostController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.example.webapp_backend.service.PostService;
import com.example.webapp_backend.service.UserService;
import com.example.webapp_backend.service.PostCategoryService;
import com.example.webapp_backend.model.dto.PostDTO;
import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.model.PostEntity;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private UserService userService;

    @Mock
    private PostCategoryService postCategoryService;

    @InjectMocks
    private PostController postController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }

    @Test
    void whenCreatePost_thenReturnsPost() throws Exception {
        // Given
        PostDTO postDTO = new PostDTO(null, "Title", "Content", "Category", null, 0, null, 0);
        UserEntity user = new UserEntity();
        user.setUsername("user");
        user.setId(1L);
        PostEntity post = new PostEntity();
        post.setId(1L);
        post.setUser(user);
        post.setTitle("Title");
        post.setContent("Content");

        given(userService.findByUsername("user")).willReturn(user);
        given(postService.createPost(any(PostEntity.class))).willReturn(post);

        // When & Then
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Title\",\"content\":\"Content\",\"category\":\"Category\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        verify(postService).createPost(any(PostEntity.class));
    }

    @Test
    void whenListPosts_thenReturnsPage() throws Exception {
        // Given
        PageRequest pageRequest = PageRequest.of(0, 10);
        PostDTO postDTO = new PostDTO(1L, "Title", "Content", "Category", null, 0, null, 0);
        given(postService.findAllPosts(pageRequest)).willReturn(new PageImpl<>(Collections.singletonList(postDTO)));

        // When & Then
        mockMvc.perform(get("/posts").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Title"));

        verify(postService).findAllPosts(any());
    }

    @Test
    void whenGetPostById_thenReturnsPost() throws Exception {
        // Given
        PostDTO postDTO = new PostDTO(1L, "Title", "Content", "Category", null, 0, null, 0);
        PostEntity post = new PostEntity();
        post.setId(1L);
        post.setTitle("Title");
        post.setContent("Content");

        given(postService.findPostById(1L)).willReturn(Optional.of(post));

        // When & Then
        mockMvc.perform(get("/posts/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"));

        verify(postService).findPostById(1L);
    }

    @Test
    void whenDeletePost_thenDeletesPost() throws Exception {
        // Given
        UserEntity user = new UserEntity();
        user.setUsername("user");
        user.setId(1L);
        PostEntity post = new PostEntity();
        post.setId(1L);
        post.setUser(user);

        given(postService.findPostById(1L)).willReturn(Optional.of(post));
        given(userService.findByUsername("user")).willReturn(user);
        doNothing().when(postService).deletePost(1L);

        // When & Then
        mockMvc.perform(delete("/posts/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(postService).deletePost(1L);
    }
}

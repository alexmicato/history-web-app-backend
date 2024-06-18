package com.example.webapp_backend.unitTests.serviceTests;

import com.example.webapp_backend.model.PostEntity;
import com.example.webapp_backend.model.dto.PostDTO;
import com.example.webapp_backend.repository.CommentRepository;
import com.example.webapp_backend.repository.LikeRepository;
import com.example.webapp_backend.repository.PostRepository;
import com.example.webapp_backend.service.PostCategoryService;
import com.example.webapp_backend.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostCategoryService postCategoryService;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPost_Success() {
        PostEntity post = new PostEntity();
        post.setTitle("Test Post");
        when(postRepository.save(any(PostEntity.class))).thenReturn(post);

        PostEntity createdPost = postService.createPost(post);
        assertEquals("Test Post", createdPost.getTitle());
        verify(postRepository).save(post);
    }

    @Test
    void deletePost_Success() {
        Long postId = 1L;
        PostEntity post = new PostEntity();
        post.setId(postId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        doNothing().when(commentRepository).deleteAll(any());
        doNothing().when(likeRepository).deleteByPostIdAndUserId(anyLong(), anyLong());

        assertDoesNotThrow(() -> postService.deletePost(postId));
        verify(postRepository).delete(post);
    }

    @Test
    void deletePost_NotFound() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> postService.deletePost(postId));
        assertEquals("Post not found", exception.getMessage());
    }

// Additional tests can be written for other methods in similar fashion.
}
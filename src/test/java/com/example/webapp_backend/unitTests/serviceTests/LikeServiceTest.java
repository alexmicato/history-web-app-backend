package com.example.webapp_backend.unitTests.serviceTests;
import com.example.webapp_backend.model.LikeEntity;
import com.example.webapp_backend.model.PostEntity;
import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.repository.LikeRepository;
import com.example.webapp_backend.repository.PostRepository;
import com.example.webapp_backend.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private LikeService likeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void likePost_Success() {
        Long postId = 1L;
        UserEntity user = new UserEntity();
        user.setId(1L);
        PostEntity post = new PostEntity();
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.existsByPostIdAndUserId(postId, user.getId())).thenReturn(false);
        when(likeRepository.save(any(LikeEntity.class))).thenReturn(new LikeEntity());

        assertDoesNotThrow(() -> likeService.likePost(postId, user));
        verify(likeRepository).save(any(LikeEntity.class));
    }

    @Test
    void likePost_AlreadyLiked() {
        Long postId = 1L;
        UserEntity user = new UserEntity();
        user.setId(1L);

        when(likeRepository.existsByPostIdAndUserId(postId, user.getId())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> likeService.likePost(postId, user));
        assertEquals("User already liked this post", exception.getMessage());
    }

    @Test
    void unlikePost_Success() {
        Long postId = 1L;
        UserEntity user = new UserEntity();
        user.setId(1L);
        LikeEntity like = new LikeEntity();
        like.setId(1L);

        when(likeRepository.findByPostIdAndUserId(postId, user.getId())).thenReturn(Optional.of(like));
        doNothing().when(likeRepository).deleteByPostIdAndUserId(postId, user.getId());

        assertDoesNotThrow(() -> likeService.unlikePost(postId, user));
        verify(likeRepository).deleteByPostIdAndUserId(postId, user.getId());
    }

    @Test
    void unlikePost_NotFound() {
        Long postId = 1L;
        UserEntity user = new UserEntity();
        user.setId(1L);

        when(likeRepository.findByPostIdAndUserId(postId, user.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> likeService.unlikePost(postId, user));
        assertEquals("Like not found", exception.getMessage());
    }

    @Test
    void userHasLiked_Success() {
        Long postId = 1L;
        UserEntity user = new UserEntity();
        user.setId(1L);

        when(likeRepository.existsByPostIdAndUserId(postId, user.getId())).thenReturn(true);

        assertTrue(likeService.userHasLiked(postId, user));
    }
}

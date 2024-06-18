package com.example.webapp_backend.unitTests.serviceTests;
import com.example.webapp_backend.exception.CustomException;
import com.example.webapp_backend.model.CommentEntity;
import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.repository.CommentRepository;
import com.example.webapp_backend.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addComment_Success() {
        CommentEntity comment = new CommentEntity();
        comment.setContent("Sample content");
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(comment);

        CommentEntity savedComment = commentService.addComment(comment);
        assertEquals("Sample content", savedComment.getContent());
        verify(commentRepository).save(comment);
    }

    @Test
    void getCommentsByPost_Success() {
        Long postId = 1L;
        CommentEntity comment1 = new CommentEntity();
        CommentEntity comment2 = new CommentEntity();
        List<CommentEntity> comments = Arrays.asList(comment1, comment2);
        when(commentRepository.findByPostId(postId)).thenReturn(comments);

        List<CommentEntity> resultComments = commentService.getCommentsByPost(postId);
        assertEquals(2, resultComments.size());
        verify(commentRepository).findByPostId(postId);
    }

    @Test
    void updateComment_Success() {
        CommentEntity comment = new CommentEntity();
        comment.setId(1L);
        comment.setContent("Updated content");
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(comment);

        CommentEntity updatedComment = commentService.updateComment(comment);
        assertEquals("Updated content", updatedComment.getContent());
        verify(commentRepository).save(comment);
    }

    @Test
    void deleteComment_Success() {
        Long commentId = 1L;
        when(commentRepository.existsById(commentId)).thenReturn(true);
        doNothing().when(commentRepository).deleteById(commentId);

        assertDoesNotThrow(() -> commentService.deleteComment(commentId));
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void deleteComment_NotFound() {
        Long commentId = 1L;
        when(commentRepository.existsById(commentId)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> commentService.deleteComment(commentId));
        assertEquals("No comment found with ID: " + commentId, exception.getMessage());
    }

    @Test
    void findCommentById_Success() {
        Long commentId = 1L;
        CommentEntity comment = new CommentEntity();
        when(commentRepository.findCommentById(commentId)).thenReturn(Optional.of(comment));

        CommentEntity foundComment = commentService.findCommentById(commentId);
        assertNotNull(foundComment);
    }

    @Test
    void findCommentById_NotFound() {
        Long commentId = 1L;
        when(commentRepository.findCommentById(commentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CustomException.class, () -> commentService.findCommentById(commentId));
        assertEquals("Comment not found with ID: " + commentId, exception.getMessage());
    }

    @Test
    void isUserCommentOwner_Success() {
        Long commentId = 1L;
        UserEntity user = new UserEntity();
        user.setUsername("john_doe");
        CommentEntity comment = new CommentEntity();
        comment.setUser(user);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        boolean isOwner = commentService.isUserCommentOwner(commentId, "john_doe");
        assertTrue(isOwner);
    }
}

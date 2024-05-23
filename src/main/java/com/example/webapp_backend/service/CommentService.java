package com.example.webapp_backend.service;


import com.example.webapp_backend.exception.CustomException;
import com.example.webapp_backend.model.CommentEntity;
import com.example.webapp_backend.model.PostEntity;
import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.repository.CommentRepository;
import com.example.webapp_backend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public CommentEntity addComment(CommentEntity comment) {

        return commentRepository.save(comment);
    }

    public List<CommentEntity> getCommentsByPost(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public CommentEntity updateComment(CommentEntity comment) {
        return commentRepository.save(comment);
    }

    // Delete a comment
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("No comment found with ID: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }

    public CommentEntity findCommentById(Long commentId)
    {
        return commentRepository.findCommentById(commentId).orElseThrow(() -> new CustomException("Comment not found with ID: " + commentId));
    }

    public boolean isUserCommentOwner(Long commentId, String username) {
        return commentRepository.findById(commentId)
                .map(CommentEntity::getUser)
                .map(UserEntity::getUsername)
                .filter(username::equals)
                .isPresent();
    }
}

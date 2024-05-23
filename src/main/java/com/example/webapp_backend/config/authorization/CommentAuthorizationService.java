package com.example.webapp_backend.config.authorization;

import com.example.webapp_backend.model.CommentEntity;
import com.example.webapp_backend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentAuthorizationService {

    @Autowired
    private CommentService commentService;

    public boolean canEditComment(Long commentId, String username) {
        CommentEntity comment = commentService.findCommentById(commentId);
        return comment.getUser().getUsername().equals(username);
    }
}


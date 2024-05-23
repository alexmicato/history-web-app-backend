package com.example.webapp_backend.config.authorization;

import com.example.webapp_backend.model.PostEntity;
import com.example.webapp_backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostAuthorizationService {

    @Autowired
    private PostService postService;

    public boolean canDeletePost(Long postId, String username) {
        PostEntity post = postService.findPostById(postId).orElseThrow(() -> new IllegalStateException("Post not found"));
        return post.getUser().getUsername().equals(username);
    }
}


package com.example.webapp_backend.controller;


import com.example.webapp_backend.model.LikeEntity;
import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.service.LikeService;
import com.example.webapp_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class LikeController {

    private final LikeService likeService;
    private final UserService userService;

    @Autowired
    public LikeController(LikeService likeService, UserService userService) {
        this.likeService = likeService;
        this.userService = userService;
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postId, Authentication authentication) {
        String username = authentication.getName();
        UserEntity user = userService.findByUsername(username);
        try {
            LikeEntity like = likeService.likePost(postId, user);
            return ResponseEntity.ok(like);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Already liked");
        }
    }

    @DeleteMapping("/{postId}/unlike")
    public ResponseEntity<?> unlikePost(@PathVariable Long postId, Authentication authentication) {
        String username = authentication.getName();
        UserEntity user = userService.findByUsername(username);
        System.out.println("Attempting to unlike post: " + postId + " by user: " + username);
        try {
            likeService.unlikePost(postId, user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{postId}/userHasLiked")
    public ResponseEntity<Boolean> userHasLiked(@PathVariable Long postId, Authentication authentication) {
        String username = authentication.getName();
        UserEntity user = userService.findByUsername(username);
        boolean hasLiked = likeService.userHasLiked(postId, user);
        return ResponseEntity.ok(hasLiked);
    }
}

package com.example.webapp_backend.controller;


import com.example.webapp_backend.model.LikeEntity;
import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.model.dto.LikeDTO;
import com.example.webapp_backend.service.LikeService;
import com.example.webapp_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
            LikeDTO likeDTO = likeService.likePost(postId, user);
            return ResponseEntity.ok(likeDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Log the exception details to help with debugging
            System.out.println("Unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @DeleteMapping("/{postId}/unlike")
    public ResponseEntity<?> unlikePost(@PathVariable Long postId, Authentication authentication) {
        String username = authentication.getName();
        UserEntity user = userService.findByUsername(username);
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

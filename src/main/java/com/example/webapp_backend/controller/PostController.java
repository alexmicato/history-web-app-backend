package com.example.webapp_backend.controller;

import com.example.webapp_backend.config.util.SecurityUtil;
import com.example.webapp_backend.exception.CustomException;
import com.example.webapp_backend.model.PostCategoryEntity;
import com.example.webapp_backend.model.PostEntity;
import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.model.data.PostCategories;
import com.example.webapp_backend.model.dto.DTOutils;
import com.example.webapp_backend.model.dto.PostDTO;
import com.example.webapp_backend.service.PostCategoryService;
import com.example.webapp_backend.service.PostService;
import com.example.webapp_backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final PostCategoryService postCategoryService;
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    public PostController(PostService postService, UserService userService, PostCategoryService postCategoryService) {
        this.postService = postService;
        this.userService = userService;
        this.postCategoryService = postCategoryService;
    }

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody PostDTO postDTO) {
        try {
            String username = SecurityUtil.getAuthenticatedUsername();
            UserEntity user = userService.findByUsername(username);
            if (user == null) {
                throw new IllegalStateException("Authenticated user not found in database");
            }

            PostEntity post = new PostEntity();
            post.setUser(user);
            post.setTitle(postDTO.getTitle());
            post.setContent(postDTO.getContent());

            // Handle category conversion from user-friendly name to entity
            if (postDTO.getCategory() != null) {
                PostCategoryEntity category = postCategoryService.findByUserFriendlyName(postDTO.getCategory());
                if (category == null) {
                    throw new IllegalArgumentException("Invalid category name provided");
                }
                post.setCategory(category);
            }

            PostEntity newPost = postService.createPost(post);

            PostDTO responseDTO = DTOutils.convertToPostDTO(newPost);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            logger.error("Failed to create post: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to create post: " + e.getMessage());
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        try {
            List<String> categories = Arrays.stream(PostCategories.values())
                    .map(PostCategories::toString)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("Failed to fetch categories: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/posts")
    public ResponseEntity<Page<PostDTO>> listPosts(Pageable pageable) {
        Page<PostDTO> posts = postService.findAllPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/users/posts/{username}")
    public ResponseEntity<Page<PostDTO>> listPostsByUser(@PathVariable String username, Pageable pageable) {
        Page<PostDTO> posts = postService.findAllUserPosts(username, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        PostEntity post = postService.findPostById(id).orElseThrow(() -> new CustomException("Post not found with ID: " + id));
        PostDTO postDTO = DTOutils.convertToPostDTO(post);
        return ResponseEntity.ok(postDTO);
    }

    @PutMapping("/posts/{id}")
    //@PreAuthorize("hasRole('MODERATOR') or @postAuthorizationService.canDeletePost(#id, principal.username)")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody PostDTO postDTO) {
        String username = SecurityUtil.getAuthenticatedUsername();

        UserEntity currentUser = userService.findByUsername(username);
        PostEntity existingPost = postService.findPostById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found with ID: " + id));

        if (!existingPost.getUser().equals(currentUser) && !SecurityUtil.isModerator(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        PostEntity updatedPost = postService.updatePost(id, postDTO);
        return ResponseEntity.ok(DTOutils.convertToPostDTO(updatedPost));
    }

    @DeleteMapping("/posts/{id}")
    //@PreAuthorize("hasRole('MODERATOR') or @postAuthorizationService.canDeletePost(#id, principal.username)")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        String username = SecurityUtil.getAuthenticatedUsername();
        UserEntity currentUser = userService.findByUsername(username);
        PostEntity post = postService.findPostById(id).orElseThrow(() -> new CustomException("Post not found with ID: " + id));

        if (!post.getUser().equals(currentUser) && !SecurityUtil.isModerator(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts/{id}/isOwner")
    public ResponseEntity<Boolean> isPostOwner(@PathVariable Long id) {
        String username = SecurityUtil.getAuthenticatedUsername();
        boolean isOwner = postService.isUserPostOwner(id, username);
        return ResponseEntity.ok(isOwner);
    }


}

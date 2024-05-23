package com.example.webapp_backend.controller;

import com.example.webapp_backend.model.dto.PostDTO;
import com.example.webapp_backend.service.PostService;
import com.example.webapp_backend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    private final UserService userService;
    private final PostService categoryService;
    private final PostService postService;


    public SearchController(UserService userService, PostService categoryService, PostService postService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.postService = postService;
    }

    @GetMapping("/search/posts")
    public ResponseEntity<Page<PostDTO>> searchPosts(
            @RequestParam String query,
            @PageableDefault Pageable pageable) {
        Page<PostDTO> result = postService.searchPosts(query, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search/popular-posts")
    public ResponseEntity<List<PostDTO>> getTop5MostPopularPosts() {
        List<PostDTO> result = postService.getTop5MostPopularPosts();
        return ResponseEntity.ok(result);
    }
}

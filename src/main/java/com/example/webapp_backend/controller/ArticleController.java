package com.example.webapp_backend.controller;


import com.example.webapp_backend.config.util.SecurityUtil;
import com.example.webapp_backend.model.ArticleEntity;
import com.example.webapp_backend.model.PostCategoryEntity;
import com.example.webapp_backend.model.PostEntity;
import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.model.data.ArticleTypes;
import com.example.webapp_backend.model.dto.ArticleDTO;
import com.example.webapp_backend.model.dto.ArticleTypeDTO;
import com.example.webapp_backend.model.dto.ReferenceDTO;
import com.example.webapp_backend.service.ArticleService;
import com.example.webapp_backend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ArticleController {

    private final ArticleService articleService;
    private final UserService userService;


    public ArticleController(ArticleService articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }

    @GetMapping("/article-types")
    public List<ArticleTypeDTO> getAllArticleTypes() {
        return List.of(ArticleTypes.values()).stream()
                .map(articleType -> new ArticleTypeDTO(articleType.toString()))
                .collect(Collectors.toList());
    }

    @PostMapping("/articles")
    public ResponseEntity<?> createArticle(@RequestBody ArticleDTO articleDTO) {
        try {
            String username = SecurityUtil.getAuthenticatedUsername();

            UserEntity user = userService.findByUsername(username);
            if (user == null || !SecurityUtil.isModerator(user)) {
                return ResponseEntity.status(403).body("Access Denied");
            }

            if (articleService.existsByTitle(articleDTO.getTitle())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Article with this title already exists");
            }

            ArticleDTO newArticle = articleService.saveArticle(articleDTO, username);

            return ResponseEntity.status(HttpStatus.CREATED).body(newArticle);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create article: " + e.getMessage());
        }
    }

    @GetMapping("/articles")
    public ResponseEntity<Page<ArticleDTO>> getAllArticles(@RequestParam int page, @RequestParam int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok(articleService.findAllArticlesSortedByTitle(pageRequest));
    }

    @GetMapping("/articles/{id}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Long id) {
        ArticleEntity article = articleService.findById(id);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }
        ArticleDTO articleDTO = new ArticleDTO(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getSummary(),
                article.getType().toString(),
                article.getEventDate(),
                article.getReadingTime(),
                article.getTags(),
                article.getReferences().stream().map(ref -> new ReferenceDTO(ref.getId(), ref.getReferenceText(), ref.getUrl())).collect(Collectors.toList())
        );
        return ResponseEntity.ok(articleDTO);
    }

    @GetMapping("/articles/type/{type}")
    public ResponseEntity<Page<ArticleDTO>> getArticlesByType(@PathVariable String type, @RequestParam int page, @RequestParam int size) {
        try {

            ArticleTypes articleType = ArticleTypes.valueOf(type);
            PageRequest pageRequest = PageRequest.of(page, size);
            return ResponseEntity.ok(articleService.findArticlesByType(articleType, pageRequest));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/articles/search")
    public ResponseEntity<Page<ArticleDTO>> searchArticles(@RequestParam String query, @RequestParam int page, @RequestParam int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok(articleService.searchArticles(query, pageRequest));
    }

    @DeleteMapping("/articles/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable Long id) {
        try {

            String username = SecurityUtil.getAuthenticatedUsername();
            UserEntity user = userService.findByUsername(username);

            if (user == null || !SecurityUtil.isModerator(user)) {
                return ResponseEntity.status(403).body("Access Denied");
            }

            ArticleEntity article = articleService.findById(id);

            if(article == null) {
                return ResponseEntity.notFound().build();
            }


            articleService.deleteArticleById(id);
            return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete article: " + e.getMessage());
        }
    }

    @PutMapping("/articles/{id}")
    public ResponseEntity<?> updateArticle(@PathVariable Long id, @RequestBody ArticleDTO articleDTO) {
        try {
            String username = SecurityUtil.getAuthenticatedUsername();
            UserEntity user = userService.findByUsername(username);

            if (user == null || !SecurityUtil.isModerator(user)) {
                return ResponseEntity.status(403).body("Access Denied");
            }

            if (articleService.existsByTitle(articleDTO.getTitle()) && !articleService.findById(id).getTitle().equals(articleDTO.getTitle())) {
                return ResponseEntity.status(409).body("Article with this title already exists");
            }

            articleDTO.setId(id);
            ArticleDTO updatedArticle = articleService.updateArticle(articleDTO, username);
            return ResponseEntity.ok(updatedArticle);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update article: " + e.getMessage());
        }
    }

    @GetMapping("/articles/event-of-the-day")
    public Optional<ArticleDTO> getEventArticleByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return articleService.getEventArticleByDate(date);
    }

    @GetMapping("/articles/recent")
    public List<ArticleDTO> getTop3RecentArticles() {
        return articleService.findTop3RecentArticles();
    }

}

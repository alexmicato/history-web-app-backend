package com.example.webapp_backend.service;


import com.example.webapp_backend.model.ArticleEntity;
import com.example.webapp_backend.model.PostEntity;
import com.example.webapp_backend.model.ReferenceEntity;
import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.model.data.ArticleTypes;
import com.example.webapp_backend.model.dto.ArticleDTO;
import com.example.webapp_backend.model.dto.ReferenceDTO;
import com.example.webapp_backend.repository.ArticleRepository;
import com.example.webapp_backend.repository.ReferenceRepository;
import com.example.webapp_backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ReferenceRepository referenceRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();


    public ArticleService(ArticleRepository articleRepository, ReferenceRepository referenceRepository, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.referenceRepository = referenceRepository;
        this.userRepository = userRepository;
    }

    public ArticleEntity findById(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    public boolean existsByTitle(String title) {
        return articleRepository.existsByTitle(title);
    }

    @Transactional
    public void deleteArticleById(Long id) {
        ArticleEntity articleEntity = articleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Article with ID " + id + " does not exist."));

        // Delete all references linked to the article
        List<ReferenceEntity> references = referenceRepository.findByArticle(articleEntity);
        referenceRepository.deleteAll(references);

        // Delete the article itself
        articleRepository.delete(articleEntity);
    }

    @Transactional
    public ArticleDTO saveArticle(ArticleDTO articleDTO, String username) {
        UserEntity author = userRepository.findByUsername(username).orElseThrow();

        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setTitle(articleDTO.getTitle());
        articleEntity.setContent(articleDTO.getContent());
        articleEntity.setSummary(articleDTO.getSummary());
        articleEntity.setType(ArticleTypes.valueOf(articleDTO.getType()));
        articleEntity.setEventDate(articleDTO.getEventDate());
        articleEntity.setReadingTime(articleDTO.getReadingTime());
        articleEntity.setTags(articleDTO.getTags());
        articleEntity.setAuthor(author);

        // Save article entity first to get its ID
        ArticleEntity savedArticleEntity = articleRepository.save(articleEntity);

        // Save references
        if (articleDTO.getReferences() != null) {
            List<ReferenceEntity> references = articleDTO.getReferences().stream().map(refDTO -> {
                ReferenceEntity refEntity = new ReferenceEntity();
                refEntity.setReferenceText(refDTO.getReferenceText());
                refEntity.setUrl(refDTO.getUrl());
                refEntity.setArticle(savedArticleEntity); // Use savedArticleEntity here
                return refEntity;
            }).collect(Collectors.toList());
            referenceRepository.saveAll(references);
        }

        // Convert back to DTO
        articleDTO.setId(articleEntity.getId());
        return articleDTO;
    }

    @Transactional
    public ArticleDTO updateArticle(ArticleDTO articleDTO, String username) {
        UserEntity author = userRepository.findByUsername(username).orElseThrow();

        ArticleEntity articleEntity = articleRepository.findById(articleDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Article with ID " + articleDTO.getId() + " does not exist."));

        articleEntity.setTitle(articleDTO.getTitle());
        articleEntity.setContent(articleDTO.getContent());
        articleEntity.setSummary(articleDTO.getSummary());
        articleEntity.setType(ArticleTypes.valueOf(articleDTO.getType()));
        articleEntity.setEventDate(articleDTO.getEventDate());
        articleEntity.setReadingTime(articleDTO.getReadingTime());
        articleEntity.setTags(articleDTO.getTags());
        articleEntity.setAuthor(author);

        // Remove old references
        referenceRepository.deleteAll(referenceRepository.findByArticle(articleEntity));

        // Save new references
        if (articleDTO.getReferences() != null) {
            List<ReferenceEntity> references = articleDTO.getReferences().stream().map(refDTO -> {
                ReferenceEntity refEntity = new ReferenceEntity();
                refEntity.setReferenceText(refDTO.getReferenceText());
                refEntity.setUrl(refDTO.getUrl());
                refEntity.setArticle(articleEntity); // Use articleEntity here
                return refEntity;
            }).collect(Collectors.toList());
            referenceRepository.saveAll(references);
        }

        // Save article entity with new data
        ArticleEntity updatedArticleEntity = articleRepository.save(articleEntity);

        // Convert back to DTO
        articleDTO.setId(updatedArticleEntity.getId());
        return articleDTO;
    }

    public Page<ArticleDTO> findAllArticlesSortedByTitle(Pageable pageable) {
        return articleRepository.findAllByOrderByTitleAsc(pageable).map(article -> new ArticleDTO(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getSummary(),
                article.getType().toString(),
                article.getEventDate(),
                article.getReadingTime(),
                article.getTags(),
                article.getReferences().stream().map(ref -> new ReferenceDTO(ref.getId(), ref.getReferenceText(), ref.getUrl())).collect(Collectors.toList())
        ));
    }

    public Page<ArticleDTO> findArticlesByType(ArticleTypes type, Pageable pageable) {
        return articleRepository.findByTypeOrderByTitleAsc(type, pageable).map(article -> new ArticleDTO(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getSummary(),
                article.getType().toString(),
                article.getEventDate(),
                article.getReadingTime(),
                article.getTags(),
                article.getReferences().stream().map(ref -> new ReferenceDTO(ref.getId(), ref.getReferenceText(), ref.getUrl())).collect(Collectors.toList())
        ));
    }

    public Page<ArticleDTO> searchArticles(String query, Pageable pageable) {
        return articleRepository.findByTitleContainingIgnoreCaseOrTagsContainingIgnoreCaseOrderByTitleAsc(query, query, pageable).map(article -> new ArticleDTO(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getSummary(),
                article.getType().toString(),
                article.getEventDate(),
                article.getReadingTime(),
                article.getTags(),
                article.getReferences().stream().map(ref -> new ReferenceDTO(ref.getId(), ref.getReferenceText(), ref.getUrl())).collect(Collectors.toList())
        ));
    }

    public List<ArticleDTO> findTop3RecentArticles() {
        List<ArticleEntity> articles = articleRepository.findTop3ByOrderByCreatedAtDesc();
        return articles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ArticleDTO> getEventArticleByDate(LocalDate eventDate) {
        int month = eventDate.getMonthValue();
        int day = eventDate.getDayOfMonth();
        List<ArticleEntity> articles = articleRepository.findByMonthAndDay(month, day);
        if (articles.isEmpty()) {
            System.out.println("No articles found for the date: " + month + " " + day);
            return Optional.empty();
        }
        ArticleEntity article = articles.get(0);

        return Optional.of(convertToDTO(article));
    }

    private ArticleDTO convertToDTO(ArticleEntity article) {
        List<ReferenceDTO> references = article.getReferences().stream()
                .map(ref -> new ReferenceDTO(ref.getId(), ref.getReferenceText(), ref.getUrl()))
                .collect(Collectors.toList());
        return new ArticleDTO(article.getId(), article.getTitle(), article.getContent(), article.getSummary(),
                article.getType().toString(), article.getEventDate(), article.getReadingTime(),
                article.getTags(), references);
    }
}

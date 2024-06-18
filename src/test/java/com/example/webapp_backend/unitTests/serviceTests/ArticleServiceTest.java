package com.example.webapp_backend.unitTests.serviceTests;

import com.example.webapp_backend.model.ArticleEntity;
import com.example.webapp_backend.model.ReferenceEntity;
import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.model.data.ArticleTypes;
import com.example.webapp_backend.model.dto.ArticleDTO;
import com.example.webapp_backend.repository.ArticleRepository;
import com.example.webapp_backend.repository.ReferenceRepository;
import com.example.webapp_backend.repository.UserRepository;
import com.example.webapp_backend.service.ArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private ReferenceRepository referenceRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ArticleService articleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void deleteArticleById_Success() {
        ArticleEntity article = new ArticleEntity();
        article.setId(1L);

        when(articleRepository.findById(anyLong())).thenReturn(Optional.of(article));
        doNothing().when(referenceRepository).deleteAll(anyList());
        doNothing().when(articleRepository).delete(any(ArticleEntity.class));

        assertDoesNotThrow(() -> articleService.deleteArticleById(1L));
        verify(articleRepository).delete(article);
    }

}


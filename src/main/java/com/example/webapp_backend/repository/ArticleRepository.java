package com.example.webapp_backend.repository;

import com.example.webapp_backend.model.ArticleEntity;
import com.example.webapp_backend.model.CommentEntity;
import com.example.webapp_backend.model.data.ArticleTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleEntity, Long>{

    Page<ArticleEntity> findAllByOrderByTitleAsc(Pageable pageable);
    Page<ArticleEntity> findByTypeOrderByTitleAsc(ArticleTypes type, Pageable pageable);
    Page<ArticleEntity> findByTitleContainingIgnoreCaseOrTagsContainingIgnoreCaseOrderByTitleAsc(String title, String tags, Pageable pageable);
    boolean existsByTitle(String title);
}

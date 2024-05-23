package com.example.webapp_backend.repository;


import com.example.webapp_backend.model.ArticleEntity;
import com.example.webapp_backend.model.ReferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReferenceRepository extends JpaRepository<ReferenceEntity, Long> {
    List<ReferenceEntity> findByArticle(ArticleEntity article);
}

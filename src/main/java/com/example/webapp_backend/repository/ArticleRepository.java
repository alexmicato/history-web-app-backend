package com.example.webapp_backend.repository;

import com.example.webapp_backend.model.ArticleEntity;
import com.example.webapp_backend.model.CommentEntity;
import com.example.webapp_backend.model.data.ArticleTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleEntity, Long>{

    Page<ArticleEntity> findAllByOrderByTitleAsc(Pageable pageable);
    Page<ArticleEntity> findByTypeOrderByTitleAsc(ArticleTypes type, Pageable pageable);
    Page<ArticleEntity> findByTitleContainingIgnoreCaseOrTagsContainingIgnoreCaseOrderByTitleAsc(String title, String tags, Pageable pageable);
    boolean existsByTitle(String title);
    List<ArticleEntity> findByEventDate(LocalDate eventDate);

    @Query("SELECT a FROM ArticleEntity a ORDER BY a.createdAt DESC")
    List<ArticleEntity> findTop3ByOrderByCreatedAtDesc();

    @Query(value = "SELECT * FROM article WHERE EXTRACT(MONTH FROM event_date) = :month AND EXTRACT(DAY FROM event_date) = :day", nativeQuery = true)
    List<ArticleEntity> findByMonthAndDay(@Param("month") int month, @Param("day") int day);
}

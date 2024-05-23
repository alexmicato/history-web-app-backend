package com.example.webapp_backend.repository;

import com.example.webapp_backend.model.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long>
{
    List<CommentEntity> findByPostId(Long postId);

    Optional<CommentEntity> findCommentById(Long id);

    @Modifying
    @Transactional
    @Query("delete from CommentEntity c where c.post.id = :postId and c.user.id = :userId")
    void deleteByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);
}
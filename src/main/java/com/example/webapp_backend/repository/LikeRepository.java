package com.example.webapp_backend.repository;


import com.example.webapp_backend.model.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long>
{
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    Optional<LikeEntity> findByPostIdAndUserId(Long postId, Long userId);

    @Modifying
    @Transactional
    @Query("delete from LikeEntity l where l.post.id = :postId and l.user.id = :userId")
    void deleteByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);
}

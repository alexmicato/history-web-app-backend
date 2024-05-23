package com.example.webapp_backend.repository;

import com.example.webapp_backend.model.PostEntity;
import com.example.webapp_backend.model.dto.PostDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long>
{
    @Query("SELECT new com.example.webapp_backend.model.dto.PostDTO(p.id, u.username, p.title, p.content, p.createdAt, count(c.id), COALESCE(p.category.name, 'Uncategorized'), count(l.id)) " +
            "FROM PostEntity p " +
            "JOIN p.user u " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.likes l " +
            "LEFT JOIN p.category pc " +
            "GROUP BY p.id, u.username, p.title, p.content, p.createdAt, pc.name")
    Page<PostDTO> findAllPostsWithCommentCount(Pageable pageable);


    @Query("SELECT new com.example.webapp_backend.model.dto.PostDTO(p.id, u.username, p.title, p.content, p.createdAt, count(c.id) as commentCount, COALESCE(pc.name, 'Uncategorized') as categoryName, count(l.id) as likeCount) " +
            "FROM PostEntity p " +
            "JOIN p.user u " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.likes l " +
            "LEFT JOIN p.category pc " +
            "WHERE u.username = :username " +
            "GROUP BY p.id, u.username, p.title, p.content, p.createdAt, pc.name")
    Page<PostDTO> findAllPostsByUser(String username, Pageable pageable);

    @Query("SELECT new com.example.webapp_backend.model.dto.PostDTO(p.id, u.username, p.title, p.content, p.createdAt, count(c.id) as commentCount, COALESCE(pc.name, 'Uncategorized') as categoryName, count(l.id) as likeCount) " +
            "FROM PostEntity p " +
            "JOIN p.user u " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.likes l " +
            "LEFT JOIN p.category pc " +
            "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(FUNCTION('REPLACE', pc.name, '_', ' ')) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "GROUP BY p.id, u.username, p.title, p.content, p.createdAt, pc.name")
    Page<PostDTO> searchPosts(String query, Pageable pageable);

    @Query("SELECT new com.example.webapp_backend.model.dto.PostDTO(p.id, u.username, p.title, p.content, p.createdAt, count(c.id) as commentCount, COALESCE(pc.name, 'Uncategorized') as categoryName, count(l.id) as likeCount) " +
            "FROM PostEntity p " +
            "JOIN p.user u " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.likes l " +
            "LEFT JOIN p.category pc " +
            "GROUP BY p.id, u.username, p.title, p.content, p.createdAt, pc.name " +
            "ORDER BY count(l.id) DESC")
    List<PostDTO> findTopByOrderByLikesCountDesc(Pageable pageable);

}
package com.example.webapp_backend.repository;


import com.example.webapp_backend.model.PostCategoryEntity;
import com.example.webapp_backend.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategoryEntity, Long>  {

    boolean existsByName(String name);
    PostCategoryEntity findByName(String name);
}

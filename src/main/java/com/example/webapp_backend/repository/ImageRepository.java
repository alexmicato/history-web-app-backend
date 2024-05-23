package com.example.webapp_backend.repository;


import com.example.webapp_backend.model.ImageEntity;
import com.example.webapp_backend.model.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long>{
}

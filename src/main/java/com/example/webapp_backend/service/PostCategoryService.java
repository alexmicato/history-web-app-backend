package com.example.webapp_backend.service;


import com.example.webapp_backend.model.PostCategoryEntity;
import com.example.webapp_backend.model.data.PostCategories;
import com.example.webapp_backend.repository.PostCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostCategoryService {

    private final PostCategoryRepository postCategoryRepository;

    public PostCategoryService(PostCategoryRepository postCategoryRepository) {
        this.postCategoryRepository = postCategoryRepository;
    }

    public PostCategoryEntity save (PostCategoryEntity postCategoryEntity){
        return postCategoryRepository.save(postCategoryEntity);
    }

    public boolean existsByName(String name){
        return postCategoryRepository.existsByName(name);
    }

    public List<PostCategoryEntity> findAll() {
        return postCategoryRepository.findAll();
    }

    public PostCategoryEntity findByName(String name) {
        return postCategoryRepository.findByName(name);
    }

    public PostCategoryEntity findByUserFriendlyName(String name) {
        PostCategories categoryEnum = PostCategories.fromString(name); // Assuming this method exists to parse user-friendly names back to enum
        return postCategoryRepository.findByName(categoryEnum.name());
    }
}

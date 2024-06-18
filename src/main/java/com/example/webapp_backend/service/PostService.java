package com.example.webapp_backend.service;


import com.example.webapp_backend.model.PostCategoryEntity;
import com.example.webapp_backend.model.data.PostCategories;
import com.example.webapp_backend.model.dto.PostDTO;
import com.example.webapp_backend.repository.CommentRepository;
import com.example.webapp_backend.repository.LikeRepository;
import com.example.webapp_backend.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.example.webapp_backend.model.PostEntity;
import com.example.webapp_backend.model.UserEntity;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final PostCategoryService postCategoryService;

    @Autowired
    public PostService(PostRepository postRepository, LikeRepository likeRepository,
                       CommentRepository commentRepository, PostCategoryService postCategoryService) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.postCategoryService = postCategoryService;
    }

    public PostEntity createPost(PostEntity post) {
        return postRepository.save(post);
    }

    public List<PostEntity> findAllPosts() {
        return postRepository.findAll();
    }

    public Optional<PostEntity> findPostById(Long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public void deletePost(Long id) {
        PostEntity post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));

        //post.getLikes().forEach(like -> like.getUser().getLikedPosts().remove(like));
        //post.getLikes().clear();
        //post.getComments().clear();

        postRepository.delete(post);
    }

    @Transactional
    public PostEntity updatePost(Long postId, PostDTO postDTO) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with ID: " + postId));

        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        PostCategoryEntity newCategory = postCategoryService.findByUserFriendlyName(postDTO.getCategory());
        post.setCategory(newCategory);

        return postRepository.save(post);
    }

    public Page<PostDTO> findAllPosts(Pageable pageable) {
        Page<PostDTO> page = postRepository.findAllPostsWithCommentCount(pageable);
        return page.map(this::convertCategoryName);
    }

    public Page<PostDTO> findAllUserPosts(String username, Pageable pageable) {
        Page<PostDTO> page = postRepository.findAllPostsByUser(username, pageable);
        return page.map(this::convertCategoryName);
    }

    private PostDTO convertCategoryName(PostDTO dto) {

        try {
            String userFriendlyCategoryName = PostCategories.valueOf(dto.getCategory()).toString();
            dto.setCategory(userFriendlyCategoryName); // Set the user-friendly name from enum
        } catch (IllegalArgumentException e) {
            dto.setCategory("Uncategorized"); // Fallback to "Uncategorized" if enum conversion fails
        }
        return dto;
    }

    public boolean isUserPostOwner(Long postId, String username) {
        return postRepository.findById(postId)
                .map(PostEntity::getUser)
                .map(UserEntity::getUsername)
                .filter(username::equals)
                .isPresent();
    }

    public Page<PostDTO> searchPosts(String query, Pageable pageable) {
        Page<PostDTO> page = postRepository.searchPosts(query, pageable);
        return page.map(this::convertCategoryName);
    }

    public List<PostDTO> getTop5MostPopularPosts() {
        Pageable topFive = PageRequest.of(0, 5);
        return postRepository.findTopByOrderByLikesCountDesc(topFive);
    }


}

package com.example.webapp_backend.service;


import com.example.webapp_backend.model.LikeEntity;
import com.example.webapp_backend.model.PostEntity;
import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.model.dto.LikeDTO;
import com.example.webapp_backend.repository.LikeRepository;
import com.example.webapp_backend.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    public LikeService(LikeRepository likeRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
    }

    public LikeDTO likePost(Long postId, UserEntity user) {
        if (likeRepository.existsByPostIdAndUserId(postId, user.getId())) {
            throw new IllegalArgumentException("User already liked this post");
        }
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        LikeEntity like = new LikeEntity();
        like.setUser(user);
        like.setPost(post);
        LikeEntity savedLike = likeRepository.save(like);
        return new LikeDTO(savedLike.getId(), user.getId(), post.getId());
    }

    public void unlikePost(Long postId, UserEntity user) {
        System.out.println("Fetching like for post ID " + postId + " and user ID " + user.getId());
        LikeEntity like = likeRepository.findByPostIdAndUserId(postId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Like not found"));
        System.out.println("Like found with ID: " + like.getId() + ", attempting to delete...");
        //likeRepository.deleteByPostIdAndUserId(postId, user.getId());
        likeRepository.delete(like);
        System.out.println("Like with ID: " + like.getId() + " deleted successfully.");
    }

    public boolean userHasLiked(Long postId, UserEntity user) {
        return likeRepository.existsByPostIdAndUserId(postId, user.getId());
    }
}

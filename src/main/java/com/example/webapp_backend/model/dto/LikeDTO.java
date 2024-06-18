package com.example.webapp_backend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeDTO {

    private Long id;
    private Long userId;
    private Long postId;

    public LikeDTO() {}

    public LikeDTO(Long id, Long userId, Long postId) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
    }
}

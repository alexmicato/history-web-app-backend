package com.example.webapp_backend.model.dto;

import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.model.data.PostCategories;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PostDTO {

    private Long id;
    private String username;
    private String title;
    private String content;
    private Date createdAt;
    private long commentCount;
    private long likesCount;
    private String category;

    public PostDTO(Long id, String username, String title, String content, Date createdAt, long commentCount, String category,
                   long likesCount) {
        this.id = id;
        this.username = username;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.commentCount = commentCount;
        this.likesCount = likesCount;
        this.category = category;
    }


}

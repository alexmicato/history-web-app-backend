package com.example.webapp_backend.model.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CommentDTO {

    private Long id;
    private String content;
    private String username;
    private Date createdAt;

    public CommentDTO(Long id, String content, String username, Date createdAt) {
        this.id = id;
        this.content = content;
        this.username = username;
        this.createdAt = createdAt;
    }

}

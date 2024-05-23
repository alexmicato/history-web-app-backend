package com.example.webapp_backend.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ChatPreviewDTO {
    private String username;
    private Date lastMessageAt;

    public ChatPreviewDTO(String username, Date lastMessageAt) {
        this.username = username;
        this.lastMessageAt = lastMessageAt;
    }
}
package com.example.webapp_backend.model.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileDTO {

    private String username;
    private String profileImageUrl;

    public UserProfileDTO(String username, String profileImageUrl) {
        this.username = username;
        this.profileImageUrl = profileImageUrl;
    }
}

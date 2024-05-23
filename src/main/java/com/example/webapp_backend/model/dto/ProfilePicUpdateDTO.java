package com.example.webapp_backend.model.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfilePicUpdateDTO {

    private String username;
    private String profilePicUrl;

    public ProfilePicUpdateDTO(String username, String profilePicUrl) {
        this.username = username;
        this.profilePicUrl = profilePicUrl;
    }

}

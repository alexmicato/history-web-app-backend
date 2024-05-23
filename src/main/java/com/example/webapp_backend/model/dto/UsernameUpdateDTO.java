package com.example.webapp_backend.model.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsernameUpdateDTO {
    private String currentUsername;
    private String newUsername;

    public UsernameUpdateDTO(String currentUsername, String newUsername)
    {
        this.currentUsername = currentUsername;
        this.newUsername = newUsername;
    }

}
package com.example.webapp_backend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordUpdateDTO {

    private String username;
    private String oldPassword;
    private String newPassword;

    public PasswordUpdateDTO(String username, String oldPassword, String newPassword)
    {
        this.username = username;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

}

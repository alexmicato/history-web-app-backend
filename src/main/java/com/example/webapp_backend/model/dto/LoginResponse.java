package com.example.webapp_backend.model.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private List<String> roles;

    public LoginResponse(String token, String username, List<String> roles) {

        this.token = token;
        this.username = username;
        this.roles = roles;
    }

}
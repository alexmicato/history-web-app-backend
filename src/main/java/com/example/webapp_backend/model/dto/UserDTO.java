package com.example.webapp_backend.model.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDTO {
    private String username;
    private List<String> roles;

    public UserDTO(String username, List<String> roles)
    {
        this.username = username;
        this.roles = roles;
    }

}

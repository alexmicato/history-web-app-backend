package com.example.webapp_backend.model.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleTypeDTO {
    private String name;

    public ArticleTypeDTO(String name) {
        this.name = name;
    }
}

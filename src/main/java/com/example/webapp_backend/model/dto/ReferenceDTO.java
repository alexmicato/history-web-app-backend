package com.example.webapp_backend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReferenceDTO {
    private Long id;
    private String referenceText;
    private String url;

    public ReferenceDTO(Long id, String referenceText, String url) {
        this.id = id;
        this.referenceText = referenceText;
        this.url = url;
    }
}

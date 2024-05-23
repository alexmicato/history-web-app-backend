package com.example.webapp_backend.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ArticleDTO {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String type;
    private LocalDate eventDate;
    private int readingTime;
    private List<String> tags;
    private List<ReferenceDTO> references;

    public ArticleDTO(Long id, String title, String content,
                      String summary, String type, LocalDate eventDate,
                      int readingTime, List<String> tags, List<ReferenceDTO> references)
    {
        this.id = id;
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.type = type;
        this.eventDate = eventDate;
        this.readingTime = readingTime;
        this.tags = tags;
        this.references = references;
    }

}
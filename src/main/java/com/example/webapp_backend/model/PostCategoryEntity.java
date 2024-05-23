package com.example.webapp_backend.model;

import com.example.webapp_backend.model.data.PostCategories;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "post_category")
public class PostCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    public String getUserFriendlyName() {
        try {
            return PostCategories.valueOf(name).toString(); // Convert enum value to user-friendly string
        } catch (IllegalArgumentException e) {
            return "Uncategorized"; // In case the enum value isn't valid
        }
    }
}

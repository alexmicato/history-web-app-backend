package com.example.webapp_backend.model.data;

public enum PostCategories {
    ANCIENT_HISTORY,
    MEDIEVAL_HISTORY,
    MODERN_HISTORY,
    WORLD_WAR,
    COLD_WAR,
    RENAISSANCE,
    INDUSTRIAL_REVOLUTION,
    OTHER;

    @Override
    public String toString() {
        // Replace underscores with spaces and capitalize each word for display
        String[] words = this.name().toLowerCase().split("_");
        StringBuilder nameBuilder = new StringBuilder();
        for (String word : words) {
            nameBuilder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }
        return nameBuilder.toString().trim();
    }

    public static PostCategories fromString(String text) {
        for (PostCategories category : PostCategories.values()) {
            if (category.toString().equalsIgnoreCase(text)) {
                return category;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}

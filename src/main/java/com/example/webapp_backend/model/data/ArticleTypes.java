package com.example.webapp_backend.model.data;

public enum ArticleTypes {
    EVENT,
    COUNTRY,
    HISTORICAL_FIGURE,
    PLACE,
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

    public static ArticleTypes fromString(String text) {
        for (ArticleTypes type: ArticleTypes.values()) {
            if (type.toString().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
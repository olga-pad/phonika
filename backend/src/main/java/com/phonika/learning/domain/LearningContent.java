package com.phonika.learning.domain;

import java.util.Objects;
import java.util.UUID;

public abstract class LearningContent {
    private final UUID id;
    private final String text;
    private final Language language;

    protected LearningContent(UUID id, String text, Language language) {
        this.id = Objects.requireNonNull(id); this.text = requireText(text); this.language = Objects.requireNonNull(language);
    }
    public UUID id() { return id; }
    public String text() { return text; }
    public Language language() { return language; }
    private static String requireText(String value) { if (value == null || value.isBlank()) throw new IllegalArgumentException("text is required"); return value; }
}

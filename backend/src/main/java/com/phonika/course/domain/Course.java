package com.phonika.course.domain;

import com.phonika.learning.domain.Language;
import com.phonika.learning.domain.LearningContent;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Course {
    private final UUID id;
    private final String code;
    private final Language language;
    private final List<LearningContent> content;

    public Course(UUID id, String code, Language language, List<LearningContent> content) {
        this.id = Objects.requireNonNull(id); this.code = requireCode(code); this.language = Objects.requireNonNull(language); this.content = List.copyOf(content);
        if (this.content.stream().anyMatch(item -> item.language() != language)) throw new IllegalArgumentException("course content language must match course language");
    }
    public UUID id() { return id; }
    public String code() { return code; }
    public Language language() { return language; }
    public List<LearningContent> content() { return content; }
    private static String requireCode(String value) { if (value == null || value.isBlank()) throw new IllegalArgumentException("code is required"); return value; }
}

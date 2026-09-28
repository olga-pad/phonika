package com.phonika.learning.domain;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** A language-scoped word with an optional explicit grapheme segmentation. */
public final class Word extends LearningContent {
    private final List<Grapheme> graphemes;

    public Word(UUID id, String text, Language language) {
        this(id, text, language, List.of());
    }

    public Word(UUID id, String text, Language language, List<Grapheme> graphemes) {
        super(id, text, language);
        this.graphemes = List.copyOf(Objects.requireNonNull(graphemes));
        if (this.graphemes.stream().anyMatch(grapheme -> grapheme.language() != language)) {
            throw new IllegalArgumentException("word grapheme language must match word language");
        }
    }

    public List<Grapheme> graphemes() {
        return graphemes;
    }
}

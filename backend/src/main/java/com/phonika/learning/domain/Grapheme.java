package com.phonika.learning.domain;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** A language-scoped written unit. A representation may contain multiple characters. */
public final class Grapheme extends LearningContent {
    private final List<Phoneme> phonemes;

    public Grapheme(UUID id, String representation, Language language) {
        this(id, representation, language, List.of());
    }

    public Grapheme(UUID id, String representation, Language language, List<Phoneme> phonemes) {
        super(id, representation, language);
        this.phonemes = List.copyOf(Objects.requireNonNull(phonemes));
        if (this.phonemes.stream().anyMatch(phoneme -> phoneme.language() != language)) {
            throw new IllegalArgumentException("grapheme and phoneme languages must match");
        }
    }

    public String representation() {
        return text();
    }

    public List<Phoneme> phonemes() {
        return phonemes;
    }
}

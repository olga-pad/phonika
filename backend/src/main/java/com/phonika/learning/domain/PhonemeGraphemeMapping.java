package com.phonika.learning.domain;

import java.util.Objects;
import java.util.UUID;

/** A language-scoped correspondence between exactly one phoneme and one grapheme. */
public final class PhonemeGraphemeMapping {
    private final UUID id;
    private final Language language;
    private final Phoneme phoneme;
    private final Grapheme grapheme;

    public PhonemeGraphemeMapping(UUID id, Language language, Phoneme phoneme, Grapheme grapheme) {
        this.id = Objects.requireNonNull(id);
        this.language = Objects.requireNonNull(language);
        this.phoneme = Objects.requireNonNull(phoneme);
        this.grapheme = Objects.requireNonNull(grapheme);

        if (phoneme.language() != language) {
            throw new IllegalArgumentException("phoneme language must match mapping language");
        }
        if (grapheme.language() != language) {
            throw new IllegalArgumentException("grapheme language must match mapping language");
        }
    }

    public UUID id() { return id; }
    public Language language() { return language; }
    public Phoneme phoneme() { return phoneme; }
    public Grapheme grapheme() { return grapheme; }
}

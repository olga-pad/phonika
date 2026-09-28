package com.phonika.learning.domain;

import java.util.UUID;

/** A language-scoped spoken unit used by reading instruction. */
public final class Phoneme extends LearningContent {
    public Phoneme(UUID id, String representation, Language language) {
        super(id, representation, language);
    }

    public String representation() {
        return text();
    }
}

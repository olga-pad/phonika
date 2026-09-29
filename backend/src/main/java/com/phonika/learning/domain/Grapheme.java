package com.phonika.learning.domain;

import java.util.UUID;

/** A language-scoped written unit. A representation may contain multiple characters. */
public final class Grapheme extends LearningContent {

    public Grapheme(UUID id, String representation, Language language) {
        super(id, representation, language);
    }

    public String representation() {
        return text();
    }
}

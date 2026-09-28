package com.phonika.learning.domain;

import java.util.UUID;

public final class Word extends LearningContent {
    public Word(UUID id, String text, Language language) { super(id, text, language); }
}

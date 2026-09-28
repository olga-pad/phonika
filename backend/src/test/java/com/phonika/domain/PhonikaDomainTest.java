package com.phonika.domain;

import com.phonika.course.domain.Course;
import com.phonika.learner.domain.Learner;
import com.phonika.learning.domain.Grapheme;
import com.phonika.learning.domain.Language;
import com.phonika.learning.domain.Phoneme;
import com.phonika.learning.domain.Word;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PhonikaDomainTest {
    @Test void supportsThreeLearningLanguages() {
        assertArrayEquals(new Language[]{Language.RU, Language.EN, Language.FR}, Language.values());
    }

    @Test void coursesAndEnrollmentsStayLanguageScoped() {
        Course ru = course("READING_RU", Language.RU);
        Course en = course("READING_EN", Language.EN);
        Course fr = course("READING_FR", Language.FR);
        assertNotEquals(ru.id(), en.id());
        assertNotEquals(en.id(), fr.id());
        Learner learner = new Learner(UUID.randomUUID(), "Learner");
        learner.enroll(UUID.randomUUID(), ru);
        learner.enroll(UUID.randomUUID(), en);
        assertEquals(2, learner.enrollments().size());
        assertSame(ru, learner.enrollments().get(0).course());
        assertSame(en, learner.enrollments().get(1).course());
    }

    @Test void phonemeGraphemeAndWordAreLanguageScoped() {
        Phoneme ruPhoneme = new Phoneme(UUID.randomUUID(), "м", Language.RU);
        Grapheme ruGrapheme = new Grapheme(UUID.randomUUID(), "м", Language.RU, List.of(ruPhoneme));
        Word ruWord = new Word(UUID.randomUUID(), "мама", Language.RU, List.of(ruGrapheme));
        assertEquals(Language.RU, ruPhoneme.language());
        assertEquals(Language.RU, ruGrapheme.language());
        assertEquals(Language.RU, ruWord.language());
    }

    @Test void graphemeSupportsMultipleCharacters() {
        Grapheme sh = new Grapheme(UUID.randomUUID(), "sh", Language.EN);
        Grapheme ou = new Grapheme(UUID.randomUUID(), "ou", Language.FR);
        assertEquals("sh", sh.representation());
        assertEquals("ou", ou.representation());
    }

    @Test void sameRepresentationInDifferentLanguagesRemainsDifferentContent() {
        Grapheme englishA = new Grapheme(UUID.randomUUID(), "A", Language.EN);
        Grapheme frenchA = new Grapheme(UUID.randomUUID(), "A", Language.FR);
        assertNotEquals(englishA.id(), frenchA.id());
        assertNotSame(englishA, frenchA);
        assertNotEquals(englishA.language(), frenchA.language());
    }

    @Test void rejectsCrossLanguageGraphemePhonemeRelation() {
        Phoneme french = new Phoneme(UUID.randomUUID(), "u", Language.FR);
        assertThrows(IllegalArgumentException.class,
                () -> new Grapheme(UUID.randomUUID(), "u", Language.EN, List.of(french)));
    }

    @Test void wordCanDescribeMultiCharacterGraphemeSegmentation() {
        Grapheme sh = new Grapheme(UUID.randomUUID(), "sh", Language.EN);
        Grapheme i = new Grapheme(UUID.randomUUID(), "i", Language.EN);
        Grapheme p = new Grapheme(UUID.randomUUID(), "p", Language.EN);
        Word ship = new Word(UUID.randomUUID(), "ship", Language.EN, List.of(sh, i, p));
        assertEquals(List.of("sh", "i", "p"), ship.graphemes().stream().map(Grapheme::representation).toList());
    }

    @Test void rejectsWordSegmentationFromAnotherLanguage() {
        Grapheme frenchOu = new Grapheme(UUID.randomUUID(), "ou", Language.FR);
        assertThrows(IllegalArgumentException.class,
                () -> new Word(UUID.randomUUID(), "out", Language.EN, List.of(frenchOu)));
    }

    @Test void rejectsContentFromAnotherLearningLanguage() {
        Word english = new Word(UUID.randomUUID(), "cat", Language.EN);
        assertThrows(IllegalArgumentException.class,
                () -> new Course(UUID.randomUUID(), "READING_RU", Language.RU, List.of(english)));
    }

    @Test void courseAcceptsOnlyMatchingLinguisticContent() {
        Phoneme phoneme = new Phoneme(UUID.randomUUID(), "м", Language.RU);
        Grapheme grapheme = new Grapheme(UUID.randomUUID(), "м", Language.RU, List.of(phoneme));
        Word word = new Word(UUID.randomUUID(), "мама", Language.RU, List.of(grapheme));
        Course course = new Course(UUID.randomUUID(), "READING_RU", Language.RU, List.of(phoneme, grapheme, word));
        assertEquals(3, course.content().size());
    }

    private Course course(String code, Language language) {
        return new Course(UUID.randomUUID(), code, language, List.of());
    }
}

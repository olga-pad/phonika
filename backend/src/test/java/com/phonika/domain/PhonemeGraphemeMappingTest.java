package com.phonika.domain;

import com.phonika.learning.domain.Grapheme;
import com.phonika.learning.domain.Language;
import com.phonika.learning.domain.Phoneme;
import com.phonika.learning.domain.PhonemeGraphemeMapping;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PhonemeGraphemeMappingTest {

    @Test
    void supportsRussianSingleLetterCorrespondence() {
        assertMapping(Language.RU, "ш", "/ш/");
    }

    @Test
    void supportsEnglishShCorrespondence() {
        assertMapping(Language.EN, "sh", "/ʃ/");
    }

    @Test
    void oneEnglishThGraphemeCanHaveMultiplePhonemeMappings() {
        Grapheme th = grapheme(Language.EN, "th");
        Phoneme voiceless = phoneme(Language.EN, "/θ/");
        Phoneme voiced = phoneme(Language.EN, "/ð/");

        PhonemeGraphemeMapping first = mapping(Language.EN, voiceless, th);
        PhonemeGraphemeMapping second = mapping(Language.EN, voiced, th);

        assertSame(th, first.grapheme());
        assertSame(th, second.grapheme());
        assertNotEquals(first.phoneme().id(), second.phoneme().id());
    }

    @Test
    void supportsEnglishIghCorrespondence() {
        assertMapping(Language.EN, "igh", "/aɪ/");
    }

    @Test
    void supportsFrenchChCorrespondence() {
        assertMapping(Language.FR, "ch", "/ʃ/");
    }

    @Test
    void supportsFrenchOuCorrespondence() {
        assertMapping(Language.FR, "ou", "/u/");
    }

    @Test
    void supportsFrenchEauCorrespondence() {
        assertMapping(Language.FR, "eau", "/o/");
    }

    @Test
    void sameRepresentationInDifferentLanguagesRemainsDifferentDomainObjects() {
        Grapheme englishCh = grapheme(Language.EN, "ch");
        Grapheme frenchCh = grapheme(Language.FR, "ch");
        Phoneme englishSound = phoneme(Language.EN, "/tʃ/");
        Phoneme frenchSound = phoneme(Language.FR, "/ʃ/");

        PhonemeGraphemeMapping english = mapping(Language.EN, englishSound, englishCh);
        PhonemeGraphemeMapping french = mapping(Language.FR, frenchSound, frenchCh);

        assertEquals("ch", english.grapheme().representation());
        assertEquals("ch", french.grapheme().representation());
        assertNotEquals(english.grapheme().id(), french.grapheme().id());
        assertNotEquals(english.id(), french.id());
    }

    @Test
    void onePhonemeCanHaveMultipleGraphemeMappings() {
        Phoneme longA = phoneme(Language.EN, "/eɪ/");
        Grapheme ai = grapheme(Language.EN, "ai");
        Grapheme ay = grapheme(Language.EN, "ay");

        PhonemeGraphemeMapping first = mapping(Language.EN, longA, ai);
        PhonemeGraphemeMapping second = mapping(Language.EN, longA, ay);

        assertSame(longA, first.phoneme());
        assertSame(longA, second.phoneme());
        assertNotEquals(first.grapheme().id(), second.grapheme().id());
    }

    @Test
    void rejectsCrossLanguageMapping() {
        Grapheme englishSh = grapheme(Language.EN, "sh");
        Phoneme frenchSh = phoneme(Language.FR, "/ʃ/");

        assertThrows(IllegalArgumentException.class,
                () -> mapping(Language.EN, frenchSh, englishSh));
    }

    @Test
    void rejectsNullRequiredValues() {
        UUID id = UUID.randomUUID();
        Phoneme phoneme = phoneme(Language.EN, "/ʃ/");
        Grapheme grapheme = grapheme(Language.EN, "sh");

        assertThrows(NullPointerException.class,
                () -> new PhonemeGraphemeMapping(null, Language.EN, phoneme, grapheme));
        assertThrows(NullPointerException.class,
                () -> new PhonemeGraphemeMapping(id, null, phoneme, grapheme));
        assertThrows(NullPointerException.class,
                () -> new PhonemeGraphemeMapping(id, Language.EN, null, grapheme));
        assertThrows(NullPointerException.class,
                () -> new PhonemeGraphemeMapping(id, Language.EN, phoneme, null));
    }

    @Test
    void rejectsEitherSideWhoseLanguageDiffersFromMappingLanguage() {
        Phoneme englishSound = phoneme(Language.EN, "/ʃ/");
        Grapheme frenchCh = grapheme(Language.FR, "ch");

        assertThrows(IllegalArgumentException.class,
                () -> mapping(Language.EN, englishSound, frenchCh));
    }

    private static void assertMapping(Language language, String graphemeValue, String phonemeValue) {
        Grapheme grapheme = grapheme(language, graphemeValue);
        Phoneme phoneme = phoneme(language, phonemeValue);
        PhonemeGraphemeMapping mapping = mapping(language, phoneme, grapheme);

        assertEquals(language, mapping.language());
        assertSame(phoneme, mapping.phoneme());
        assertSame(grapheme, mapping.grapheme());
    }

    private static Grapheme grapheme(Language language, String representation) {
        return new Grapheme(UUID.randomUUID(), representation, language);
    }

    private static Phoneme phoneme(Language language, String representation) {
        return new Phoneme(UUID.randomUUID(), representation, language);
    }

    private static PhonemeGraphemeMapping mapping(Language language, Phoneme phoneme, Grapheme grapheme) {
        return new PhonemeGraphemeMapping(UUID.randomUUID(), language, phoneme, grapheme);
    }
}

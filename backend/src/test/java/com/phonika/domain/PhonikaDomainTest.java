package com.phonika.domain;

import com.phonika.course.domain.Course;
import com.phonika.learner.domain.Learner;
import com.phonika.learning.application.ContentAvailability;
import com.phonika.learning.domain.Grapheme;
import com.phonika.learning.domain.Language;
import com.phonika.learning.domain.Phoneme;
import com.phonika.learning.domain.Skill;
import com.phonika.learning.domain.Word;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PhonikaDomainTest {
    private final ContentAvailability availability = new ContentAvailability();

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

    @Test void russianWordIsAvailableWhenAllExplicitPrerequisiteSkillsAreMastered() {
        RussianKot fixture = russianKot();
        assertTrue(availability.isAvailable(fixture.course(), fixture.word(),
                Set.of(fixture.kSkill().id(), fixture.oSkill().id(), fixture.tSkill().id())));
    }

    @Test void russianWordIsLockedWhenOneExplicitPrerequisiteSkillIsMissing() {
        RussianKot fixture = russianKot();
        assertFalse(availability.isAvailable(fixture.course(), fixture.word(),
                Set.of(fixture.kSkill().id(), fixture.oSkill().id())));
    }

    @Test void availabilityDoesNotInspectWordCharactersOrSegmentation() {
        UUID courseId = UUID.randomUUID();
        Phoneme prerequisite = new Phoneme(UUID.randomUUID(), "z", Language.RU);
        Word word = new Word(UUID.randomUUID(), "кот", Language.RU);
        Skill prerequisiteSkill = new Skill(UUID.randomUUID(), courseId, prerequisite);
        Skill wordSkill = new Skill(UUID.randomUUID(), courseId, word, Set.of(prerequisiteSkill.id()));
        Course course = new Course(courseId, "READING_RU", Language.RU,
                List.of(prerequisite, word), List.of(prerequisiteSkill, wordSkill));

        assertTrue(word.graphemes().isEmpty());
        assertTrue(availability.isAvailable(course, word, Set.of(prerequisiteSkill.id())));
    }

    @Test void courseRejectsSkillTargetFromAnotherLanguage() {
        UUID courseId = UUID.randomUUID();
        Word english = new Word(UUID.randomUUID(), "cat", Language.EN);
        Skill skill = new Skill(UUID.randomUUID(), courseId, english);
        assertThrows(IllegalArgumentException.class,
                () -> new Course(courseId, "READING_RU", Language.RU, List.of(english), List.of(skill)));
    }

    @Test void courseRejectsPrerequisiteSkillOutsideItsOwnSkillSet() {
        UUID ruCourseId = UUID.randomUUID();
        UUID enCourseId = UUID.randomUUID();
        Phoneme ruK = new Phoneme(UUID.randomUUID(), "к", Language.RU);
        Word kot = new Word(UUID.randomUUID(), "кот", Language.RU);
        Grapheme sh = new Grapheme(UUID.randomUUID(), "sh", Language.EN);
        Skill englishSh = new Skill(UUID.randomUUID(), enCourseId, sh);
        Skill ruKSkill = new Skill(UUID.randomUUID(), ruCourseId, ruK);
        Skill kotSkill = new Skill(UUID.randomUUID(), ruCourseId, kot, Set.of(englishSh.id()));

        assertThrows(IllegalArgumentException.class,
                () -> new Course(ruCourseId, "READING_RU", Language.RU,
                        List.of(ruK, kot), List.of(ruKSkill, kotSkill)));
    }

    @Test void availabilitySupportsMultiCharacterGraphemeSkill() {
        UUID courseId = UUID.randomUUID();
        Grapheme sh = new Grapheme(UUID.randomUUID(), "sh", Language.EN);
        Word ship = new Word(UUID.randomUUID(), "ship", Language.EN);
        Skill shSkill = new Skill(UUID.randomUUID(), courseId, sh);
        Skill shipSkill = new Skill(UUID.randomUUID(), courseId, ship, Set.of(shSkill.id()));
        Course course = new Course(courseId, "READING_EN", Language.EN,
                List.of(sh, ship), List.of(shSkill, shipSkill));

        assertEquals("sh", sh.representation());
        assertTrue(availability.isAvailable(course, ship, Set.of(shSkill.id())));
    }

    private RussianKot russianKot() {
        UUID courseId = UUID.randomUUID();
        Phoneme k = new Phoneme(UUID.randomUUID(), "к", Language.RU);
        Phoneme o = new Phoneme(UUID.randomUUID(), "о", Language.RU);
        Phoneme t = new Phoneme(UUID.randomUUID(), "т", Language.RU);
        Word kot = new Word(UUID.randomUUID(), "кот", Language.RU);
        Skill kSkill = new Skill(UUID.randomUUID(), courseId, k);
        Skill oSkill = new Skill(UUID.randomUUID(), courseId, o);
        Skill tSkill = new Skill(UUID.randomUUID(), courseId, t);
        Skill kotSkill = new Skill(UUID.randomUUID(), courseId, kot,
                Set.of(kSkill.id(), oSkill.id(), tSkill.id()));
        Course course = new Course(courseId, "READING_RU", Language.RU,
                List.of(k, o, t, kot), List.of(kSkill, oSkill, tSkill, kotSkill));
        return new RussianKot(course, kot, kSkill, oSkill, tSkill);
    }

    private Course course(String code, Language language) {
        return new Course(UUID.randomUUID(), code, language, List.of());
    }

    private record RussianKot(Course course, Word word, Skill kSkill, Skill oSkill, Skill tSkill) {}
}

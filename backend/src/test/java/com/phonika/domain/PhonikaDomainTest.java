package com.phonika.domain;

import com.phonika.course.domain.Course;
import com.phonika.learner.domain.Learner;
import com.phonika.learning.domain.Language;
import com.phonika.learning.domain.Word;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class PhonikaDomainTest {
    @Test void supportsThreeLearningLanguages() { assertArrayEquals(new Language[]{Language.RU, Language.EN, Language.FR}, Language.values()); }
    @Test void coursesAndEnrollmentsStayLanguageScoped() {
        Course ru = course("READING_RU", Language.RU); Course en = course("READING_EN", Language.EN); Course fr = course("READING_FR", Language.FR);
        assertNotEquals(ru.id(), en.id()); assertNotEquals(en.id(), fr.id());
        Learner learner = new Learner(UUID.randomUUID(), "Learner"); learner.enroll(UUID.randomUUID(), ru); learner.enroll(UUID.randomUUID(), en);
        assertEquals(2, learner.enrollments().size()); assertSame(ru, learner.enrollments().get(0).course()); assertSame(en, learner.enrollments().get(1).course());
    }
    @Test void rejectsContentFromAnotherLearningLanguage() {
        Word english = new Word(UUID.randomUUID(), "cat", Language.EN);
        assertThrows(IllegalArgumentException.class, () -> new Course(UUID.randomUUID(), "READING_RU", Language.RU, List.of(english)));
    }
    private Course course(String code, Language language) { return new Course(UUID.randomUUID(), code, language, List.of()); }
}

package com.phonika.domain;

import com.phonika.course.domain.Course;
import com.phonika.learner.domain.Enrollment;
import com.phonika.learner.domain.Learner;
import com.phonika.learner.domain.SkillProgress;
import com.phonika.learner.domain.SkillProgressStatus;
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

class SkillProgressTest {
    private final ContentAvailability availability = new ContentAvailability();

    @Test void newProgressStartsNotStartedAndTransitionsToMastered() {
        Fixture fixture = russianKot();
        Learner learner = new Learner(UUID.randomUUID(), "Thomas");
        Enrollment enrollment = learner.enroll(UUID.randomUUID(), fixture.course());
        SkillProgress progress = learner.trackSkill(UUID.randomUUID(), enrollment, fixture.k());
        assertEquals(SkillProgressStatus.NOT_STARTED, progress.status());
        progress.startLearning();
        assertEquals(SkillProgressStatus.LEARNING, progress.status());
        progress.markMastered();
        assertEquals(SkillProgressStatus.MASTERED, progress.status());
    }

    @Test void learnerAndSkillProgressAreIndependent() {
        Fixture fixture = russianKot();
        Learner thomas = new Learner(UUID.randomUUID(), "Thomas");
        Learner anna = new Learner(UUID.randomUUID(), "Anna");
        Enrollment thomasEnrollment = thomas.enroll(UUID.randomUUID(), fixture.course());
        Enrollment annaEnrollment = anna.enroll(UUID.randomUUID(), fixture.course());
        SkillProgress thomasK = thomas.trackSkill(UUID.randomUUID(), thomasEnrollment, fixture.k());
        SkillProgress thomasO = thomas.trackSkill(UUID.randomUUID(), thomasEnrollment, fixture.o());
        SkillProgress annaK = anna.trackSkill(UUID.randomUUID(), annaEnrollment, fixture.k());
        thomasK.startLearning(); thomasK.markMastered();
        thomasO.startLearning();
        assertEquals(SkillProgressStatus.MASTERED, thomasK.status());
        assertEquals(SkillProgressStatus.LEARNING, thomasO.status());
        assertEquals(SkillProgressStatus.NOT_STARTED, annaK.status());
    }

    @Test void rejectsProgressForAnotherCourseAndDuplicateSkillProgress() {
        Fixture ru = russianKot();
        UUID enCourseId = UUID.randomUUID();
        Grapheme sh = new Grapheme(UUID.randomUUID(), "sh", Language.EN);
        Skill shSkill = new Skill(UUID.randomUUID(), enCourseId, sh);
        Course en = new Course(enCourseId, "READING_EN", Language.EN, List.of(sh), List.of(shSkill));
        Learner learner = new Learner(UUID.randomUUID(), "Thomas");
        Enrollment ruEnrollment = learner.enroll(UUID.randomUUID(), ru.course());
        assertThrows(IllegalArgumentException.class,
                () -> learner.trackSkill(UUID.randomUUID(), ruEnrollment, shSkill));
        learner.trackSkill(UUID.randomUUID(), ruEnrollment, ru.k());
        assertThrows(IllegalStateException.class,
                () -> learner.trackSkill(UUID.randomUUID(), ruEnrollment, ru.k()));
        assertNotNull(en);
    }

    @Test void availabilityUsesOnlyMasteredLearnerProgress() {
        Fixture fixture = russianKot();
        Learner learner = new Learner(UUID.randomUUID(), "Thomas");
        Enrollment enrollment = learner.enroll(UUID.randomUUID(), fixture.course());
        SkillProgress k = learner.trackSkill(UUID.randomUUID(), enrollment, fixture.k());
        SkillProgress o = learner.trackSkill(UUID.randomUUID(), enrollment, fixture.o());
        SkillProgress t = learner.trackSkill(UUID.randomUUID(), enrollment, fixture.t());
        k.startLearning(); k.markMastered();
        o.startLearning(); o.markMastered();
        t.startLearning();
        assertFalse(availability.isAvailable(fixture.course(), fixture.word(), learner));
        t.markMastered();
        assertTrue(availability.isAvailable(fixture.course(), fixture.word(), learner));
    }

    @Test void multiCharacterGraphemeIsOneSkillProgressTarget() {
        UUID courseId = UUID.randomUUID();
        Grapheme sh = new Grapheme(UUID.randomUUID(), "sh", Language.EN);
        Word ship = new Word(UUID.randomUUID(), "ship", Language.EN);
        Skill shSkill = new Skill(UUID.randomUUID(), courseId, sh);
        Skill shipSkill = new Skill(UUID.randomUUID(), courseId, ship, Set.of(shSkill.id()));
        Course course = new Course(courseId, "READING_EN", Language.EN, List.of(sh, ship), List.of(shSkill, shipSkill));
        Learner learner = new Learner(UUID.randomUUID(), "Learner");
        Enrollment enrollment = learner.enroll(UUID.randomUUID(), course);
        SkillProgress shProgress = learner.trackSkill(UUID.randomUUID(), enrollment, shSkill);
        shProgress.startLearning(); shProgress.markMastered();
        assertEquals("sh", sh.representation());
        assertTrue(availability.isAvailable(course, ship, learner));
    }

    private Fixture russianKot() {
        UUID courseId = UUID.randomUUID();
        Phoneme k = new Phoneme(UUID.randomUUID(), "к", Language.RU);
        Phoneme o = new Phoneme(UUID.randomUUID(), "о", Language.RU);
        Phoneme t = new Phoneme(UUID.randomUUID(), "т", Language.RU);
        Word kot = new Word(UUID.randomUUID(), "кот", Language.RU);
        Skill kSkill = new Skill(UUID.randomUUID(), courseId, k);
        Skill oSkill = new Skill(UUID.randomUUID(), courseId, o);
        Skill tSkill = new Skill(UUID.randomUUID(), courseId, t);
        Skill kotSkill = new Skill(UUID.randomUUID(), courseId, kot, Set.of(kSkill.id(), oSkill.id(), tSkill.id()));
        Course course = new Course(courseId, "READING_RU", Language.RU,
                List.of(k, o, t, kot), List.of(kSkill, oSkill, tSkill, kotSkill));
        return new Fixture(course, kot, kSkill, oSkill, tSkill);
    }

    private record Fixture(Course course, Word word, Skill k, Skill o, Skill t) {}
}

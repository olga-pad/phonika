package com.phonika.domain;

import com.phonika.course.domain.Course;
import com.phonika.learner.domain.Enrollment;
import com.phonika.learner.domain.Learner;
import com.phonika.learning.domain.Assistance;
import com.phonika.learning.domain.EvidenceResult;
import com.phonika.learning.domain.EvidenceSource;
import com.phonika.learning.domain.Grapheme;
import com.phonika.learning.domain.Language;
import com.phonika.learning.domain.LearningEvidence;
import com.phonika.learning.domain.Phoneme;
import com.phonika.learning.domain.Skill;
import com.phonika.learning.domain.SkillKind;
import com.phonika.learning.domain.Word;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LearningEvidenceTest {
    private static final Instant NOW = Instant.parse("2026-09-29T12:00:00Z");

    @Test void sameWordCanHaveRecognitionAndIndependentReadingSkills() {
        Fixture fixture = russianKot();
        assertEquals(fixture.recognition().target().id(), fixture.independentReading().target().id());
        assertEquals(SkillKind.RECOGNITION, fixture.recognition().kind());
        assertEquals(SkillKind.INDEPENDENT_READING, fixture.independentReading().kind());
        assertNotEquals(fixture.recognition().id(), fixture.independentReading().id());
    }

    @Test void courseRejectsDuplicateTargetAndKindButIdentityRemainsSkillId() {
        UUID courseId = UUID.randomUUID();
        Word kot = new Word(UUID.randomUUID(), "кот", Language.RU);
        Skill first = new Skill(UUID.randomUUID(), courseId, kot, SkillKind.RECOGNITION);
        Skill duplicate = new Skill(UUID.randomUUID(), courseId, kot, SkillKind.RECOGNITION);
        assertNotEquals(first.id(), duplicate.id());
        assertThrows(IllegalArgumentException.class,
                () -> new Course(courseId, "READING_RU", Language.RU, List.of(kot), List.of(first, duplicate)));
    }

    @Test void firstAttemptIndependentSuccessIsRepresentedExplicitly() {
        Context context = enrolled(russianKot());
        LearningEvidence evidence = evidence(context, context.fixture().recognition(), EvidenceResult.CORRECT,
                true, Assistance.NONE, EvidenceSource.GAME);
        assertTrue(evidence.correct());
        assertTrue(evidence.firstAttempt());
        assertEquals(Assistance.NONE, evidence.assistance());
    }

    @Test void wrongAnswerIsPedagogicalEvidence() {
        Context context = enrolled(russianKot());
        LearningEvidence evidence = evidence(context, context.fixture().recognition(), EvidenceResult.INCORRECT,
                true, Assistance.NONE, EvidenceSource.GAME);
        assertFalse(evidence.correct());
        assertEquals(EvidenceResult.INCORRECT, evidence.result());
    }

    @Test void wrongThenCorrectDoesNotBecomeFirstAttemptSuccess() {
        Context context = enrolled(russianKot());
        LearningEvidence wrong = evidence(context, context.fixture().recognition(), EvidenceResult.INCORRECT,
                true, Assistance.NONE, EvidenceSource.GAME);
        LearningEvidence correct = evidence(context, context.fixture().recognition(), EvidenceResult.CORRECT,
                false, Assistance.NONE, EvidenceSource.GAME);
        assertFalse(wrong.correct());
        assertTrue(correct.correct());
        assertFalse(correct.firstAttempt());
    }

    @Test void assistedSuccessIsNotIndependentSuccess() {
        Context context = enrolled(russianKot());
        LearningEvidence evidence = evidence(context, context.fixture().recognition(), EvidenceResult.CORRECT,
                true, Assistance.HINT, EvidenceSource.GAME);
        assertTrue(evidence.correct());
        assertEquals(Assistance.HINT, evidence.assistance());
    }

    @Test void evidenceIsLearnerAndSkillSpecific() {
        Fixture fixture = russianKot();
        Context thomas = enrolled(fixture);
        Learner annaLearner = new Learner(UUID.randomUUID(), "Anna");
        Enrollment annaEnrollment = annaLearner.enroll(UUID.randomUUID(), fixture.course());
        Context anna = new Context(fixture, annaLearner, annaEnrollment);

        LearningEvidence recognition = evidence(thomas, fixture.recognition(), EvidenceResult.CORRECT,
                true, Assistance.NONE, EvidenceSource.GAME);
        LearningEvidence annaRecognition = evidence(anna, fixture.recognition(), EvidenceResult.CORRECT,
                true, Assistance.NONE, EvidenceSource.GAME);
        LearningEvidence reading = evidence(thomas, fixture.independentReading(), EvidenceResult.CORRECT,
                true, Assistance.NONE, EvidenceSource.PARENT_CONFIRMATION);

        assertNotEquals(recognition.learnerId(), annaRecognition.learnerId());
        assertNotEquals(recognition.skillId(), reading.skillId());
        assertEquals(fixture.recognition().id(), recognition.skillId());
        assertEquals(fixture.independentReading().id(), reading.skillId());
    }

    @Test void recognitionEvidenceDoesNotConfirmIndependentReadingOfSameWord() {
        Context context = enrolled(russianKot());
        LearningEvidence recognition = evidence(context, context.fixture().recognition(), EvidenceResult.CORRECT,
                true, Assistance.NONE, EvidenceSource.GAME);
        assertEquals(context.fixture().recognition().id(), recognition.skillId());
        assertNotEquals(context.fixture().independentReading().id(), recognition.skillId());
    }

    @Test void rejectsEvidenceForSkillOutsideLearnerEnrollmentCourse() {
        Context ru = enrolled(russianKot());
        UUID enCourseId = UUID.randomUUID();
        Grapheme sh = new Grapheme(UUID.randomUUID(), "sh", Language.EN);
        Skill shSkill = new Skill(UUID.randomUUID(), enCourseId, sh, SkillKind.RECOGNITION);
        new Course(enCourseId, "READING_EN", Language.EN, List.of(sh), List.of(shSkill));

        assertThrows(IllegalArgumentException.class, () -> new LearningEvidence(
                UUID.randomUUID(), ru.learner(), ru.enrollment(), shSkill, NOW,
                EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME, null));
    }

    @Test void multiCharacterEnglishGraphemeIsOneEvidenceSkillTarget() {
        UUID courseId = UUID.randomUUID();
        Grapheme sh = new Grapheme(UUID.randomUUID(), "sh", Language.EN);
        Skill shSkill = new Skill(UUID.randomUUID(), courseId, sh, SkillKind.RECOGNITION);
        Course course = new Course(courseId, "READING_EN", Language.EN, List.of(sh), List.of(shSkill));
        Learner learner = new Learner(UUID.randomUUID(), "Learner");
        Enrollment enrollment = learner.enroll(UUID.randomUUID(), course);
        LearningEvidence evidence = new LearningEvidence(UUID.randomUUID(), learner, enrollment, shSkill, NOW,
                EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME, "activity-1");

        assertEquals("sh", sh.representation());
        assertEquals(shSkill.id(), evidence.skillId());
        assertEquals("activity-1", evidence.activityReference().orElseThrow());
    }

    private LearningEvidence evidence(Context context, Skill skill, EvidenceResult result,
                                      boolean firstAttempt, Assistance assistance, EvidenceSource source) {
        return new LearningEvidence(UUID.randomUUID(), context.learner(), context.enrollment(), skill, NOW,
                result, firstAttempt, assistance, source, null);
    }

    private Context enrolled(Fixture fixture) {
        Learner learner = new Learner(UUID.randomUUID(), "Thomas");
        Enrollment enrollment = learner.enroll(UUID.randomUUID(), fixture.course());
        return new Context(fixture, learner, enrollment);
    }

    private Fixture russianKot() {
        UUID courseId = UUID.randomUUID();
        Phoneme k = new Phoneme(UUID.randomUUID(), "к", Language.RU);
        Word kot = new Word(UUID.randomUUID(), "кот", Language.RU);
        Skill recognition = new Skill(UUID.randomUUID(), courseId, kot, SkillKind.RECOGNITION);
        Skill independentReading = new Skill(UUID.randomUUID(), courseId, kot, SkillKind.INDEPENDENT_READING);
        Course course = new Course(courseId, "READING_RU", Language.RU,
                List.of(k, kot), List.of(recognition, independentReading));
        return new Fixture(course, kot, recognition, independentReading);
    }

    private record Fixture(Course course, Word word, Skill recognition, Skill independentReading) {}
    private record Context(Fixture fixture, Learner learner, Enrollment enrollment) {}
}

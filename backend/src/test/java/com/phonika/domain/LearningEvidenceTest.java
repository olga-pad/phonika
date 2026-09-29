package com.phonika.domain;

import com.phonika.course.domain.Course;
import com.phonika.learner.domain.Enrollment;
import com.phonika.learner.domain.Learner;
import com.phonika.learning.domain.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Test void twoSessionsForSameLearnerHaveIndependentIdentity() {
        Context context = enrolled(russianKot());
        LearningSession first = session(context);
        LearningSession second = session(context);
        assertNotEquals(first.id(), second.id());
        assertEquals(first.learnerId(), second.learnerId());
        assertEquals(first.enrollmentId(), second.enrollmentId());
        assertEquals(first.courseId(), second.courseId());
    }

    @Test void multipleEvidenceCanBelongToOneSession() {
        Context context = enrolled(russianKot());
        LearningSession session = session(context);
        LearningEvidence one = evidence(context, context.fixture().recognition(), session, EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME);
        LearningEvidence two = evidence(context, context.fixture().recognition(), session, EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME);
        LearningEvidence three = evidence(context, context.fixture().recognition(), session, EvidenceResult.INCORRECT, true, Assistance.NONE, EvidenceSource.GAME);
        assertEquals(Set.of(session.id()), Set.of(one.learningSessionId(), two.learningSessionId(), three.learningSessionId()));
    }

    @Test void evidenceAcrossTwoSessionsHasTwoDistinctSessionIds() {
        Context context = enrolled(russianKot());
        LearningSession a = session(context);
        LearningSession b = session(context);
        List<LearningEvidence> evidence = List.of(
                evidence(context, context.fixture().recognition(), a, EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME),
                evidence(context, context.fixture().recognition(), a, EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME),
                evidence(context, context.fixture().recognition(), b, EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME));
        Set<UUID> sessionIds = evidence.stream().map(LearningEvidence::learningSessionId).collect(Collectors.toSet());
        assertEquals(2, sessionIds.size());
    }

    @Test void oneSessionCanContainEvidenceForDifferentSkills() {
        Context context = enrolled(russianKot());
        LearningSession session = session(context);
        LearningEvidence recognition = evidence(context, context.fixture().recognition(), session, EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME);
        LearningEvidence reading = evidence(context, context.fixture().independentReading(), session, EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.PARENT_CONFIRMATION);
        assertEquals(session.id(), recognition.learningSessionId());
        assertEquals(session.id(), reading.learningSessionId());
        assertNotEquals(recognition.skillId(), reading.skillId());
    }

    @Test void firstAttemptIndependentSuccessIsRepresentedExplicitly() {
        Context context = enrolled(russianKot());
        LearningEvidence evidence = evidence(context, context.fixture().recognition(), session(context), EvidenceResult.CORRECT,
                true, Assistance.NONE, EvidenceSource.GAME);
        assertTrue(evidence.correct());
        assertTrue(evidence.firstAttempt());
        assertEquals(Assistance.NONE, evidence.assistance());
    }

    @Test void wrongAnswerIsPedagogicalEvidence() {
        Context context = enrolled(russianKot());
        LearningEvidence evidence = evidence(context, context.fixture().recognition(), session(context), EvidenceResult.INCORRECT,
                true, Assistance.NONE, EvidenceSource.GAME);
        assertFalse(evidence.correct());
        assertEquals(EvidenceResult.INCORRECT, evidence.result());
    }

    @Test void wrongThenCorrectDoesNotBecomeFirstAttemptSuccess() {
        Context context = enrolled(russianKot());
        LearningSession session = session(context);
        LearningEvidence wrong = evidence(context, context.fixture().recognition(), session, EvidenceResult.INCORRECT,
                true, Assistance.NONE, EvidenceSource.GAME);
        LearningEvidence correct = evidence(context, context.fixture().recognition(), session, EvidenceResult.CORRECT,
                false, Assistance.NONE, EvidenceSource.GAME);
        assertFalse(wrong.correct());
        assertTrue(correct.correct());
        assertFalse(correct.firstAttempt());
    }

    @Test void assistedSuccessIsNotIndependentSuccess() {
        Context context = enrolled(russianKot());
        LearningEvidence evidence = evidence(context, context.fixture().recognition(), session(context), EvidenceResult.CORRECT,
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
        LearningEvidence recognition = evidence(thomas, fixture.recognition(), session(thomas), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME);
        LearningEvidence annaRecognition = evidence(anna, fixture.recognition(), session(anna), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME);
        LearningEvidence reading = evidence(thomas, fixture.independentReading(), session(thomas), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.PARENT_CONFIRMATION);
        assertNotEquals(recognition.learnerId(), annaRecognition.learnerId());
        assertNotEquals(recognition.skillId(), reading.skillId());
    }

    @Test void recognitionEvidenceDoesNotConfirmIndependentReadingOfSameWord() {
        Context context = enrolled(russianKot());
        LearningEvidence recognition = evidence(context, context.fixture().recognition(), session(context), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME);
        assertEquals(context.fixture().recognition().id(), recognition.skillId());
        assertNotEquals(context.fixture().independentReading().id(), recognition.skillId());
    }

    @Test void rejectsEvidenceForSkillOutsideLearnerEnrollmentCourse() {
        Context ru = enrolled(russianKot());
        UUID enCourseId = UUID.randomUUID();
        Grapheme sh = new Grapheme(UUID.randomUUID(), "sh", Language.EN);
        Skill shSkill = new Skill(UUID.randomUUID(), enCourseId, sh, SkillKind.RECOGNITION);
        new Course(enCourseId, "READING_EN", Language.EN, List.of(sh), List.of(shSkill));
        assertThrows(IllegalArgumentException.class, () -> new LearningEvidence(UUID.randomUUID(), ru.learner(), ru.enrollment(), shSkill,
                session(ru), NOW, EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME, null));
    }

    @Test void rejectsSessionFromAnotherLearner() {
        Fixture fixture = russianKot();
        Context thomas = enrolled(fixture);
        Context anna = enrolled(fixture);
        LearningSession annaSession = session(anna);
        assertThrows(IllegalArgumentException.class, () -> new LearningEvidence(UUID.randomUUID(), thomas.learner(), thomas.enrollment(),
                fixture.recognition(), annaSession, NOW, EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME, null));
    }

    @Test void rejectsSessionFromAnotherEnrollment() {
        Fixture fixture = russianKot();
        Learner learner = new Learner(UUID.randomUUID(), "Thomas");
        Enrollment first = learner.enroll(UUID.randomUUID(), fixture.course());
        Enrollment second = learner.enroll(UUID.randomUUID(), fixture.course());
        Context firstContext = new Context(fixture, learner, first);
        LearningSession firstSession = session(firstContext);
        assertThrows(IllegalArgumentException.class, () -> new LearningEvidence(UUID.randomUUID(), learner, second,
                fixture.recognition(), firstSession, NOW, EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME, null));
    }

    @Test void rejectsSessionFromAnotherCourse() {
        Context ru = enrolled(russianKot());
        UUID enCourseId = UUID.randomUUID();
        Grapheme sh = new Grapheme(UUID.randomUUID(), "sh", Language.EN);
        Skill shSkill = new Skill(UUID.randomUUID(), enCourseId, sh, SkillKind.RECOGNITION);
        Course enCourse = new Course(enCourseId, "READING_EN", Language.EN, List.of(sh), List.of(shSkill));
        Learner learner = ru.learner();
        Enrollment enEnrollment = learner.enroll(UUID.randomUUID(), enCourse);
        LearningSession enSession = new LearningSession(UUID.randomUUID(), learner, enEnrollment, NOW);
        assertThrows(IllegalArgumentException.class, () -> new LearningEvidence(UUID.randomUUID(), learner, ru.enrollment(),
                ru.fixture().recognition(), enSession, NOW, EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME, null));
    }

    @Test void multiCharacterEnglishGraphemeIsOneEvidenceSkillTarget() {
        UUID courseId = UUID.randomUUID();
        Grapheme sh = new Grapheme(UUID.randomUUID(), "sh", Language.EN);
        Skill shSkill = new Skill(UUID.randomUUID(), courseId, sh, SkillKind.RECOGNITION);
        Course course = new Course(courseId, "READING_EN", Language.EN, List.of(sh), List.of(shSkill));
        Learner learner = new Learner(UUID.randomUUID(), "Learner");
        Enrollment enrollment = learner.enroll(UUID.randomUUID(), course);
        LearningSession session = new LearningSession(UUID.randomUUID(), learner, enrollment, NOW);
        LearningEvidence evidence = new LearningEvidence(UUID.randomUUID(), learner, enrollment, shSkill, session, NOW,
                EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME, "activity-1");
        assertEquals("sh", sh.representation());
        assertEquals(shSkill.id(), evidence.skillId());
        assertEquals(session.id(), evidence.learningSessionId());
        assertEquals("activity-1", evidence.activityReference().orElseThrow());
    }

    private LearningEvidence evidence(Context context, Skill skill, LearningSession session, EvidenceResult result,
                                      boolean firstAttempt, Assistance assistance, EvidenceSource source) {
        return new LearningEvidence(UUID.randomUUID(), context.learner(), context.enrollment(), skill, session, NOW,
                result, firstAttempt, assistance, source, null);
    }

    private LearningSession session(Context context) {
        return new LearningSession(UUID.randomUUID(), context.learner(), context.enrollment(), NOW);
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
        Course course = new Course(courseId, "READING_RU", Language.RU, List.of(k, kot), List.of(recognition, independentReading));
        return new Fixture(course, kot, recognition, independentReading);
    }

    private record Fixture(Course course, Word word, Skill recognition, Skill independentReading) {}
    private record Context(Fixture fixture, Learner learner, Enrollment enrollment) {}
}

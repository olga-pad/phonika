package com.phonika.domain;

import com.phonika.course.domain.Course;
import com.phonika.learner.domain.*;
import com.phonika.learning.application.ContentAvailability;
import com.phonika.learning.application.MasteryProgression;
import com.phonika.learning.domain.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MasteryPolicyTest {
    private static final Instant NOW = Instant.parse("2026-09-29T12:00:00Z");
    private final MasteryPolicy policy = new MasteryPolicy();
    private final MasteryProgression progression = new MasteryProgression(policy);

    @Test void oneSuccessfulSessionIsNotMastered() {
        Context c = context(SkillKind.RECOGNITION, Language.RU, "кот");
        assertFalse(policy.isMastered(c.learner, c.enrollment, c.skill,
                List.of(evidence(c, session(c), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME))));
    }

    @Test void manySuccessesInOneSessionStillCountOnce() {
        Context c = context(SkillKind.RECOGNITION, Language.RU, "кот");
        LearningSession s = session(c);
        List<LearningEvidence> evidence = new ArrayList<>();
        for (int i = 0; i < 5; i++) evidence.add(evidence(c, s, EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME));
        assertFalse(policy.isMastered(c.learner, c.enrollment, c.skill, evidence));
    }

    @Test void twoSuccessfulSessionsMasterRecognition() {
        Context c = context(SkillKind.RECOGNITION, Language.RU, "кот");
        assertTrue(policy.isMastered(c.learner, c.enrollment, c.skill, List.of(
                evidence(c, session(c), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME),
                evidence(c, session(c), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME))));
    }

    @Test void wrongThenCorrectIsNotSuccessfulSession() {
        Context c = context(SkillKind.RECOGNITION, Language.RU, "кот");
        LearningSession a = session(c);
        LearningSession b = session(c);
        assertFalse(policy.isMastered(c.learner, c.enrollment, c.skill, List.of(
                evidence(c, a, EvidenceResult.INCORRECT, true, Assistance.NONE, EvidenceSource.GAME),
                evidence(c, a, EvidenceResult.CORRECT, false, Assistance.NONE, EvidenceSource.GAME),
                evidence(c, b, EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME))));
    }

    @Test void hintDoesNotQualify() {
        Context c = context(SkillKind.RECOGNITION, Language.RU, "кот");
        assertFalse(policy.isMastered(c.learner, c.enrollment, c.skill, List.of(
                evidence(c, session(c), EvidenceResult.CORRECT, true, Assistance.HINT, EvidenceSource.GAME),
                evidence(c, session(c), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME))));
    }

    @Test void errorBetweenSuccessfulSessionsDoesNotEraseHistory() {
        Context c = context(SkillKind.RECOGNITION, Language.RU, "кот");
        assertTrue(policy.isMastered(c.learner, c.enrollment, c.skill, List.of(
                evidence(c, session(c), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME),
                evidence(c, session(c), EvidenceResult.INCORRECT, true, Assistance.NONE, EvidenceSource.GAME),
                evidence(c, session(c), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME))));
    }

    @Test void gameDoesNotMasterIndependentReadingButReadingSourcesDo() {
        Context c = context(SkillKind.INDEPENDENT_READING, Language.RU, "кот");
        assertFalse(policy.isMastered(c.learner, c.enrollment, c.skill, List.of(
                evidence(c, session(c), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME),
                evidence(c, session(c), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME))));
        assertTrue(policy.isMastered(c.learner, c.enrollment, c.skill, List.of(
                evidence(c, session(c), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.INDEPENDENT_READING),
                evidence(c, session(c), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.PARENT_CONFIRMATION))));
    }

    @Test void sameTargetDifferentSkillsRemainIndependent() {
        UUID courseId = UUID.randomUUID();
        Word kot = new Word(UUID.randomUUID(), "кот", Language.RU);
        Skill recognition = new Skill(UUID.randomUUID(), courseId, kot, SkillKind.RECOGNITION);
        Skill reading = new Skill(UUID.randomUUID(), courseId, kot, SkillKind.INDEPENDENT_READING);
        Course course = new Course(courseId, "READING_RU", Language.RU, List.of(kot), List.of(recognition, reading));
        Learner learner = new Learner(UUID.randomUUID(), "Thomas");
        Enrollment enrollment = learner.enroll(UUID.randomUUID(), course);
        Context r = new Context(learner, enrollment, recognition);
        List<LearningEvidence> evidence = List.of(
                evidence(r, session(r), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME),
                evidence(r, session(r), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME));
        assertTrue(policy.isMastered(learner, enrollment, recognition, evidence));
        assertFalse(policy.isMastered(learner, enrollment, reading, evidence));
    }

    @Test void learnerAndCourseEvidenceAreIsolated() {
        Context thomas = context(SkillKind.RECOGNITION, Language.RU, "кот");
        Learner anna = new Learner(UUID.randomUUID(), "Anna");
        Enrollment annaEnrollment = anna.enroll(UUID.randomUUID(), thomas.enrollment.course());
        List<LearningEvidence> thomasEvidence = List.of(
                evidence(thomas, session(thomas), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME),
                evidence(thomas, session(thomas), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME));
        assertFalse(policy.isMastered(anna, annaEnrollment, thomas.skill, thomasEvidence));

        Context en = context(SkillKind.RECOGNITION, Language.EN, "sh");
        assertFalse(policy.isMastered(thomas.learner, thomas.enrollment, thomas.skill, List.of(
                evidence(en, session(en), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME),
                evidence(en, session(en), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME))));
    }

    @Test void masteredProgressRemainsMasteredAfterLaterError() {
        Context c = context(SkillKind.RECOGNITION, Language.RU, "кот");
        SkillProgress progress = c.learner.trackSkill(UUID.randomUUID(), c.enrollment, c.skill);
        List<LearningEvidence> successes = List.of(
                evidence(c, session(c), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME),
                evidence(c, session(c), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME));
        progression.apply(c.learner, c.enrollment, c.skill, progress, successes);
        assertEquals(SkillProgressStatus.MASTERED, progress.status());
        progression.apply(c.learner, c.enrollment, c.skill, progress, List.of(
                evidence(c, session(c), EvidenceResult.INCORRECT, true, Assistance.NONE, EvidenceSource.GAME)));
        assertEquals(SkillProgressStatus.MASTERED, progress.status());
    }

    @Test void multiCharacterEnglishGraphemeIsOneMasteryTarget() {
        Context c = context(SkillKind.RECOGNITION, Language.EN, "sh");
        assertEquals("sh", c.skill.target().representation());
        assertTrue(policy.isMastered(c.learner, c.enrollment, c.skill, List.of(
                evidence(c, session(c), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME),
                evidence(c, session(c), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME))));
    }

    @Test void masteringFinalPrerequisiteUnlocksExistingContentAvailability() {
        UUID courseId = UUID.randomUUID();
        Phoneme k = new Phoneme(UUID.randomUUID(), "к", Language.RU);
        Phoneme o = new Phoneme(UUID.randomUUID(), "о", Language.RU);
        Phoneme t = new Phoneme(UUID.randomUUID(), "т", Language.RU);
        Word kot = new Word(UUID.randomUUID(), "кот", Language.RU);
        Skill ks = new Skill(UUID.randomUUID(), courseId, k);
        Skill os = new Skill(UUID.randomUUID(), courseId, o);
        Skill ts = new Skill(UUID.randomUUID(), courseId, t);
        Skill word = new Skill(UUID.randomUUID(), courseId, kot, Set.of(ks.id(), os.id(), ts.id()));
        Course course = new Course(courseId, "READING_RU", Language.RU, List.of(k, o, t, kot), List.of(ks, os, ts, word));
        Learner learner = new Learner(UUID.randomUUID(), "Thomas");
        Enrollment enrollment = learner.enroll(UUID.randomUUID(), course);
        SkillProgress kp = learner.trackSkill(UUID.randomUUID(), enrollment, ks);
        SkillProgress op = learner.trackSkill(UUID.randomUUID(), enrollment, os);
        SkillProgress tp = learner.trackSkill(UUID.randomUUID(), enrollment, ts);
        kp.startLearning(); kp.markMastered(); op.startLearning(); op.markMastered(); tp.startLearning();
        ContentAvailability availability = new ContentAvailability();
        assertFalse(availability.isAvailable(course, kot, learner));
        Context tc = new Context(learner, enrollment, ts);
        progression.apply(learner, enrollment, ts, tp, List.of(
                evidence(tc, session(tc), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME),
                evidence(tc, session(tc), EvidenceResult.CORRECT, true, Assistance.NONE, EvidenceSource.GAME)));
        assertEquals(SkillProgressStatus.MASTERED, tp.status());
        assertTrue(availability.isAvailable(course, kot, learner));
    }

    private Context context(SkillKind kind, Language language, String representation) {
        UUID courseId = UUID.randomUUID();
        LearningContent target = language == Language.EN ? new Grapheme(UUID.randomUUID(), representation, language)
                : new Word(UUID.randomUUID(), representation, language);
        Skill skill = new Skill(UUID.randomUUID(), courseId, target, kind);
        Course course = new Course(courseId, "READING_" + language, language, List.of(target), List.of(skill));
        Learner learner = new Learner(UUID.randomUUID(), "Learner");
        Enrollment enrollment = learner.enroll(UUID.randomUUID(), course);
        return new Context(learner, enrollment, skill);
    }

    private LearningSession session(Context c) {
        return new LearningSession(UUID.randomUUID(), c.learner, c.enrollment, NOW);
    }

    private LearningEvidence evidence(Context c, LearningSession session, EvidenceResult result, boolean firstAttempt,
                                      Assistance assistance, EvidenceSource source) {
        return new LearningEvidence(UUID.randomUUID(), c.learner, c.enrollment, c.skill, session, NOW,
                result, firstAttempt, assistance, source, null);
    }

    private record Context(Learner learner, Enrollment enrollment, Skill skill) {}
}

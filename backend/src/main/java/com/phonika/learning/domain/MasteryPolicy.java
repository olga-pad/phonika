package com.phonika.learning.domain;

import com.phonika.learner.domain.Enrollment;
import com.phonika.learner.domain.Learner;

import java.util.Collection;
import java.util.Objects;

/** Transparent V1 mastery rule based on independent success in distinct learning sessions. */
public final class MasteryPolicy {
    private static final long REQUIRED_SUCCESSFUL_SESSIONS = 2;

    public boolean isMastered(Learner learner, Enrollment enrollment, Skill skill,
                              Collection<LearningEvidence> evidence) {
        Objects.requireNonNull(learner);
        Objects.requireNonNull(enrollment);
        Objects.requireNonNull(skill);
        Objects.requireNonNull(evidence);

        boolean ownsEnrollment = learner.enrollments().stream()
                .anyMatch(candidate -> candidate.id().equals(enrollment.id()));
        if (!ownsEnrollment) {
            throw new IllegalArgumentException("enrollment must belong to learner");
        }
        if (!enrollment.course().id().equals(skill.courseId())
                || enrollment.course().skills().stream().noneMatch(candidate -> candidate.id().equals(skill.id()))) {
            throw new IllegalArgumentException("skill must belong to enrollment course");
        }

        long successfulSessions = evidence.stream()
                .filter(item -> item.learnerId().equals(learner.id()))
                .filter(item -> item.enrollmentId().equals(enrollment.id()))
                .filter(item -> item.courseId().equals(enrollment.course().id()))
                .filter(item -> item.skillId().equals(skill.id()))
                .filter(item -> qualifiesFor(skill, item))
                .map(LearningEvidence::learningSessionId)
                .distinct()
                .count();

        return successfulSessions >= REQUIRED_SUCCESSFUL_SESSIONS;
    }

    public boolean isQualifying(Skill skill, LearningEvidence evidence) {
        Objects.requireNonNull(skill);
        Objects.requireNonNull(evidence);
        return evidence.skillId().equals(skill.id()) && qualifiesFor(skill, evidence);
    }

    private boolean qualifiesFor(Skill skill, LearningEvidence evidence) {
        return evidence.result() == EvidenceResult.CORRECT
                && evidence.firstAttempt()
                && evidence.assistance() == Assistance.NONE
                && sourceSupports(skill.kind(), evidence.source());
    }

    private boolean sourceSupports(SkillKind kind, EvidenceSource source) {
        return switch (kind) {
            case RECOGNITION -> source == EvidenceSource.GAME;
            case INDEPENDENT_READING -> source == EvidenceSource.INDEPENDENT_READING
                    || source == EvidenceSource.PARENT_CONFIRMATION;
        };
    }
}

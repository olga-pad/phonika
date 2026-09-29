package com.phonika.learning.domain;

import com.phonika.learner.domain.Enrollment;
import com.phonika.learner.domain.Learner;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Immutable pedagogical observation about one learner and one concrete skill. */
public final class LearningEvidence {
    private final UUID id;
    private final UUID learnerId;
    private final UUID enrollmentId;
    private final UUID courseId;
    private final UUID skillId;
    private final Instant occurredAt;
    private final EvidenceResult result;
    private final boolean firstAttempt;
    private final Assistance assistance;
    private final EvidenceSource source;
    private final String activityReference;

    public LearningEvidence(
            UUID id,
            Learner learner,
            Enrollment enrollment,
            Skill skill,
            Instant occurredAt,
            EvidenceResult result,
            boolean firstAttempt,
            Assistance assistance,
            EvidenceSource source,
            String activityReference) {
        this.id = Objects.requireNonNull(id);
        Objects.requireNonNull(learner);
        Objects.requireNonNull(enrollment);
        Objects.requireNonNull(skill);
        this.occurredAt = Objects.requireNonNull(occurredAt);
        this.result = Objects.requireNonNull(result);
        this.assistance = Objects.requireNonNull(assistance);
        this.source = Objects.requireNonNull(source);

        boolean ownsEnrollment = learner.enrollments().stream()
                .anyMatch(candidate -> candidate.id().equals(enrollment.id()));
        if (!ownsEnrollment) {
            throw new IllegalArgumentException("enrollment must belong to learner");
        }
        if (!enrollment.course().id().equals(skill.courseId())
                || enrollment.course().skills().stream().noneMatch(candidate -> candidate.id().equals(skill.id()))) {
            throw new IllegalArgumentException("skill must belong to enrollment course");
        }
        if (activityReference != null && activityReference.isBlank()) {
            throw new IllegalArgumentException("activity reference must be non-blank when provided");
        }

        this.learnerId = learner.id();
        this.enrollmentId = enrollment.id();
        this.courseId = enrollment.course().id();
        this.skillId = skill.id();
        this.firstAttempt = firstAttempt;
        this.activityReference = activityReference;
    }

    public UUID id() { return id; }
    public UUID learnerId() { return learnerId; }
    public UUID enrollmentId() { return enrollmentId; }
    public UUID courseId() { return courseId; }
    public UUID skillId() { return skillId; }
    public Instant occurredAt() { return occurredAt; }
    public EvidenceResult result() { return result; }
    public boolean correct() { return result == EvidenceResult.CORRECT; }
    public boolean firstAttempt() { return firstAttempt; }
    public Assistance assistance() { return assistance; }
    public EvidenceSource source() { return source; }
    public Optional<String> activityReference() { return Optional.ofNullable(activityReference); }
}

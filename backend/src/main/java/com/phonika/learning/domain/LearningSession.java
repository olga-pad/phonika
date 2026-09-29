package com.phonika.learning.domain;

import com.phonika.learner.domain.Enrollment;
import com.phonika.learner.domain.Learner;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Identity and context of one explicit learning episode. */
public final class LearningSession {
    private final UUID id;
    private final UUID learnerId;
    private final UUID enrollmentId;
    private final UUID courseId;
    private final Instant startedAt;

    public LearningSession(UUID id, Learner learner, Enrollment enrollment, Instant startedAt) {
        this.id = Objects.requireNonNull(id);
        Objects.requireNonNull(learner);
        Objects.requireNonNull(enrollment);
        this.startedAt = Objects.requireNonNull(startedAt);

        boolean ownsEnrollment = learner.enrollments().stream()
                .anyMatch(candidate -> candidate.id().equals(enrollment.id()));
        if (!ownsEnrollment) {
            throw new IllegalArgumentException("enrollment must belong to learner");
        }

        this.learnerId = learner.id();
        this.enrollmentId = enrollment.id();
        this.courseId = enrollment.course().id();
    }

    public UUID id() { return id; }
    public UUID learnerId() { return learnerId; }
    public UUID enrollmentId() { return enrollmentId; }
    public UUID courseId() { return courseId; }
    public Instant startedAt() { return startedAt; }
}

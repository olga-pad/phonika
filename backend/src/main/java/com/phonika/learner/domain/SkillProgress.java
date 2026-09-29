package com.phonika.learner.domain;

import com.phonika.learning.domain.Skill;
import java.util.Objects;
import java.util.UUID;

/** Current state of one learner for one course-scoped skill. */
public final class SkillProgress {
    private final UUID id;
    private final UUID learnerId;
    private final UUID enrollmentId;
    private final UUID courseId;
    private final UUID skillId;
    private SkillProgressStatus status;

    SkillProgress(UUID id, UUID learnerId, Enrollment enrollment, Skill skill) {
        this.id = Objects.requireNonNull(id);
        this.learnerId = Objects.requireNonNull(learnerId);
        Objects.requireNonNull(enrollment);
        Objects.requireNonNull(skill);
        if (!enrollment.course().id().equals(skill.courseId())) {
            throw new IllegalArgumentException("skill must belong to enrollment course");
        }
        this.enrollmentId = enrollment.id();
        this.courseId = enrollment.course().id();
        this.skillId = skill.id();
        this.status = SkillProgressStatus.NOT_STARTED;
    }

    public void startLearning() {
        if (status != SkillProgressStatus.NOT_STARTED) {
            throw new IllegalStateException("only not-started skill progress can start learning");
        }
        status = SkillProgressStatus.LEARNING;
    }

    public void markMastered() {
        if (status != SkillProgressStatus.LEARNING) {
            throw new IllegalStateException("only learning skill progress can become mastered");
        }
        status = SkillProgressStatus.MASTERED;
    }

    public UUID id() { return id; }
    public UUID learnerId() { return learnerId; }
    public UUID enrollmentId() { return enrollmentId; }
    public UUID courseId() { return courseId; }
    public UUID skillId() { return skillId; }
    public SkillProgressStatus status() { return status; }
    public boolean isMastered() { return status == SkillProgressStatus.MASTERED; }
}

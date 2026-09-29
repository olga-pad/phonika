package com.phonika.learning.application;

import com.phonika.learner.domain.Enrollment;
import com.phonika.learner.domain.Learner;
import com.phonika.learner.domain.SkillProgress;
import com.phonika.learner.domain.SkillProgressStatus;
import com.phonika.learning.domain.LearningEvidence;
import com.phonika.learning.domain.MasteryPolicy;
import com.phonika.learning.domain.Skill;

import java.util.Collection;
import java.util.Objects;

/** Applies evidence-derived mastery decisions to existing monotonic SkillProgress. */
public final class MasteryProgression {
    private final MasteryPolicy policy;

    public MasteryProgression(MasteryPolicy policy) {
        this.policy = Objects.requireNonNull(policy);
    }

    public void apply(Learner learner, Enrollment enrollment, Skill skill, SkillProgress progress,
                      Collection<LearningEvidence> evidence) {
        Objects.requireNonNull(progress);
        Objects.requireNonNull(evidence);
        if (!progress.learnerId().equals(learner.id())
                || !progress.enrollmentId().equals(enrollment.id())
                || !progress.courseId().equals(enrollment.course().id())
                || !progress.skillId().equals(skill.id())) {
            throw new IllegalArgumentException("skill progress must match learner enrollment and skill");
        }
        if (progress.status() == SkillProgressStatus.MASTERED) {
            return;
        }

        boolean hasPedagogicalEvidence = evidence.stream()
                .anyMatch(item -> item.learnerId().equals(learner.id())
                        && item.enrollmentId().equals(enrollment.id())
                        && item.courseId().equals(enrollment.course().id())
                        && item.skillId().equals(skill.id()));
        if (progress.status() == SkillProgressStatus.NOT_STARTED && hasPedagogicalEvidence) {
            progress.startLearning();
        }
        if (progress.status() == SkillProgressStatus.LEARNING
                && policy.isMastered(learner, enrollment, skill, evidence)) {
            progress.markMastered();
        }
    }
}

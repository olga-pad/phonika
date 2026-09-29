package com.phonika.learner.domain;

import com.phonika.course.domain.Course;
import com.phonika.learning.domain.Skill;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class Learner {
    private final UUID id;
    private final String displayName;
    private final List<Enrollment> enrollments = new ArrayList<>();
    private final List<SkillProgress> skillProgress = new ArrayList<>();

    public Learner(UUID id, String displayName) {
        this.id = Objects.requireNonNull(id);
        this.displayName = Objects.requireNonNull(displayName);
    }

    public Enrollment enroll(UUID enrollmentId, Course course) {
        boolean duplicate = enrollments.stream().anyMatch(e -> e.course().id().equals(course.id()));
        if (duplicate) throw new IllegalStateException("learner is already enrolled in this course");
        Enrollment enrollment = new Enrollment(enrollmentId, course);
        enrollments.add(enrollment);
        return enrollment;
    }

    public SkillProgress trackSkill(UUID progressId, Enrollment enrollment, Skill skill) {
        Objects.requireNonNull(enrollment);
        Objects.requireNonNull(skill);
        boolean ownsEnrollment = enrollments.stream().anyMatch(e -> e.id().equals(enrollment.id()));
        if (!ownsEnrollment) throw new IllegalArgumentException("enrollment must belong to learner");
        if (!enrollment.course().skills().stream().anyMatch(candidate -> candidate.id().equals(skill.id()))) {
            throw new IllegalArgumentException("skill must belong to enrollment course");
        }
        boolean duplicate = skillProgress.stream().anyMatch(progress -> progress.skillId().equals(skill.id()));
        if (duplicate) throw new IllegalStateException("learner already has progress for this skill");
        SkillProgress progress = new SkillProgress(progressId, id, enrollment, skill);
        skillProgress.add(progress);
        return progress;
    }

    public Set<UUID> masteredSkillIds(UUID courseId) {
        Objects.requireNonNull(courseId);
        return skillProgress.stream()
                .filter(progress -> progress.courseId().equals(courseId) && progress.isMastered())
                .map(SkillProgress::skillId)
                .collect(Collectors.toUnmodifiableSet());
    }

    public UUID id() { return id; }
    public String displayName() { return displayName; }
    public List<Enrollment> enrollments() { return Collections.unmodifiableList(enrollments); }
    public List<SkillProgress> skillProgress() { return Collections.unmodifiableList(skillProgress); }
}

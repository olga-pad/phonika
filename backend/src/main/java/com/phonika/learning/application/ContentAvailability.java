package com.phonika.learning.application;

import com.phonika.course.domain.Course;
import com.phonika.learning.domain.LearningContent;
import com.phonika.learning.domain.Skill;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/** Evaluates pedagogical availability from explicit skill prerequisites only. */
public final class ContentAvailability {
    public boolean isAvailable(Course course, LearningContent content, Set<UUID> masteredSkillIds) {
        Objects.requireNonNull(course);
        Objects.requireNonNull(content);
        Objects.requireNonNull(masteredSkillIds);

        Skill skill = course.skillTargeting(content)
                .orElseThrow(() -> new IllegalArgumentException("content has no skill in this course"));
        return masteredSkillIds.containsAll(skill.prerequisiteSkillIds());
    }
}

package com.phonika.learning.domain;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/** A course-scoped pedagogical skill targeting one piece of learning content. */
public final class Skill {
    private final UUID id;
    private final UUID courseId;
    private final LearningContent target;
    private final Set<UUID> prerequisiteSkillIds;

    public Skill(UUID id, UUID courseId, LearningContent target) {
        this(id, courseId, target, Set.of());
    }

    public Skill(UUID id, UUID courseId, LearningContent target, Set<UUID> prerequisiteSkillIds) {
        this.id = Objects.requireNonNull(id);
        this.courseId = Objects.requireNonNull(courseId);
        this.target = Objects.requireNonNull(target);
        this.prerequisiteSkillIds = Set.copyOf(new LinkedHashSet<>(Objects.requireNonNull(prerequisiteSkillIds)));
        if (this.prerequisiteSkillIds.contains(this.id)) {
            throw new IllegalArgumentException("skill cannot require itself");
        }
    }

    public UUID id() { return id; }
    public UUID courseId() { return courseId; }
    public LearningContent target() { return target; }
    public Set<UUID> prerequisiteSkillIds() { return prerequisiteSkillIds; }
}

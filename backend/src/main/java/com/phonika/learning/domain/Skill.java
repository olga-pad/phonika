package com.phonika.learning.domain;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/** A course-scoped pedagogical skill: an action performed on learning content. */
public final class Skill {
    private final UUID id;
    private final UUID courseId;
    private final LearningContent target;
    private final SkillKind kind;
    private final Set<UUID> prerequisiteSkillIds;

    public Skill(UUID id, UUID courseId, LearningContent target) {
        this(id, courseId, target, SkillKind.RECOGNITION, Set.of());
    }

    public Skill(UUID id, UUID courseId, LearningContent target, Set<UUID> prerequisiteSkillIds) {
        this(id, courseId, target, SkillKind.RECOGNITION, prerequisiteSkillIds);
    }

    public Skill(UUID id, UUID courseId, LearningContent target, SkillKind kind) {
        this(id, courseId, target, kind, Set.of());
    }

    public Skill(UUID id, UUID courseId, LearningContent target, SkillKind kind, Set<UUID> prerequisiteSkillIds) {
        this.id = Objects.requireNonNull(id);
        this.courseId = Objects.requireNonNull(courseId);
        this.target = Objects.requireNonNull(target);
        this.kind = Objects.requireNonNull(kind);
        this.prerequisiteSkillIds = Set.copyOf(new LinkedHashSet<>(Objects.requireNonNull(prerequisiteSkillIds)));
        if (this.prerequisiteSkillIds.contains(this.id)) {
            throw new IllegalArgumentException("skill cannot require itself");
        }
    }

    public UUID id() { return id; }
    public UUID courseId() { return courseId; }
    public LearningContent target() { return target; }
    public SkillKind kind() { return kind; }
    public Set<UUID> prerequisiteSkillIds() { return prerequisiteSkillIds; }
}

package com.phonika.course.domain;

import com.phonika.learning.domain.Language;
import com.phonika.learning.domain.LearningContent;
import com.phonika.learning.domain.Skill;
import com.phonika.learning.domain.SkillKind;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class Course {
    private final UUID id;
    private final String code;
    private final Language language;
    private final List<LearningContent> content;
    private final List<Skill> skills;

    public Course(UUID id, String code, Language language, List<LearningContent> content) {
        this(id, code, language, content, List.of());
    }

    public Course(UUID id, String code, Language language, List<LearningContent> content, List<Skill> skills) {
        this.id = Objects.requireNonNull(id);
        this.code = requireCode(code);
        this.language = Objects.requireNonNull(language);
        this.content = List.copyOf(Objects.requireNonNull(content));
        this.skills = List.copyOf(Objects.requireNonNull(skills));

        if (this.content.stream().anyMatch(item -> item.language() != language)) {
            throw new IllegalArgumentException("course content language must match course language");
        }

        Set<UUID> contentIds = new HashSet<>();
        for (LearningContent item : this.content) {
            if (!contentIds.add(item.id())) throw new IllegalArgumentException("course content ids must be unique");
        }

        Set<UUID> skillIds = new HashSet<>();
        Set<SkillDefinition> skillDefinitions = new HashSet<>();
        for (Skill skill : this.skills) {
            if (!skillIds.add(skill.id())) throw new IllegalArgumentException("course skill ids must be unique");
            if (!skill.courseId().equals(this.id)) throw new IllegalArgumentException("skill must belong to this course");
            if (skill.target().language() != language) throw new IllegalArgumentException("skill target language must match course language");
            if (!contentIds.contains(skill.target().id())) throw new IllegalArgumentException("skill target must belong to course content");
            if (!skillDefinitions.add(new SkillDefinition(skill.target().id(), skill.kind()))) {
                throw new IllegalArgumentException("course cannot contain duplicate target and skill kind");
            }
        }
        for (Skill skill : this.skills) {
            if (!skillIds.containsAll(skill.prerequisiteSkillIds())) {
                throw new IllegalArgumentException("skill prerequisites must belong to the same course");
            }
        }
    }

    public UUID id() { return id; }
    public String code() { return code; }
    public Language language() { return language; }
    public List<LearningContent> content() { return content; }
    public List<Skill> skills() { return skills; }

    public Optional<Skill> skillTargeting(LearningContent target) {
        Objects.requireNonNull(target);
        return skills.stream().filter(skill -> skill.target().id().equals(target.id())).findFirst();
    }

    public Optional<Skill> skillTargeting(LearningContent target, SkillKind kind) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(kind);
        return skills.stream()
                .filter(skill -> skill.target().id().equals(target.id()) && skill.kind() == kind)
                .findFirst();
    }

    private static String requireCode(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("code is required");
        return value;
    }

    private record SkillDefinition(UUID targetId, SkillKind kind) {}
}

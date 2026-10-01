package com.phonika.course.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;

/** Course-scoped pedagogical ordering of grapheme introductions. */
public final class Curriculum {
    private final UUID courseId;
    private final List<CurriculumLevel> levels;
    private final Map<UUID, Integer> introductionLevels;

    public Curriculum(UUID courseId, List<CurriculumLevel> levels) {
        this.courseId = Objects.requireNonNull(courseId);
        Objects.requireNonNull(levels);
        if (levels.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("curriculum levels cannot contain null");
        }

        List<CurriculumLevel> ordered = new ArrayList<>(levels);
        ordered.sort(Comparator.comparingInt(CurriculumLevel::number));

        Set<Integer> levelNumbers = new HashSet<>();
        Map<UUID, Integer> introductions = new HashMap<>();
        for (CurriculumLevel level : ordered) {
            if (!levelNumbers.add(level.number())) {
                throw new IllegalArgumentException("curriculum level numbers must be unique");
            }
            for (UUID graphemeId : level.introducedGraphemeIds()) {
                if (introductions.putIfAbsent(graphemeId, level.number()) != null) {
                    throw new IllegalArgumentException("grapheme can be introduced only once in a curriculum");
                }
            }
        }

        this.levels = List.copyOf(ordered);
        this.introductionLevels = Map.copyOf(introductions);
    }

    public UUID courseId() { return courseId; }
    public List<CurriculumLevel> levels() { return levels; }

    public OptionalInt introductionLevel(UUID graphemeId) {
        Objects.requireNonNull(graphemeId);
        Integer level = introductionLevels.get(graphemeId);
        return level == null ? OptionalInt.empty() : OptionalInt.of(level);
    }
}

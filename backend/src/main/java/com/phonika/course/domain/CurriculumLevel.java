package com.phonika.course.domain;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/** One ordered curriculum level and the graphemes first introduced at that level. */
public final class CurriculumLevel {
    private final int number;
    private final Set<UUID> introducedGraphemeIds;

    public CurriculumLevel(int number, Set<UUID> introducedGraphemeIds) {
        if (number <= 0) throw new IllegalArgumentException("curriculum level number must be positive");
        Objects.requireNonNull(introducedGraphemeIds);
        if (introducedGraphemeIds.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("introduced grapheme ids cannot contain null");
        }
        this.number = number;
        this.introducedGraphemeIds = Set.copyOf(new LinkedHashSet<>(introducedGraphemeIds));
    }

    public int number() { return number; }
    public Set<UUID> introducedGraphemeIds() { return introducedGraphemeIds; }
}

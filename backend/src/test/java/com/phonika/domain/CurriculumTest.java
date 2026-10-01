package com.phonika.domain;

import com.phonika.course.domain.Curriculum;
import com.phonika.course.domain.CurriculumLevel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurriculumTest {

    @Test void courseIdIsRequired() {
        assertThrows(NullPointerException.class, () -> new Curriculum(null, List.of()));
    }

    @Test void curriculumOrdersLevelsByNumber() {
        Curriculum curriculum = new Curriculum(UUID.randomUUID(), List.of(level(2), level(1), level(3)));
        assertEquals(List.of(1, 2, 3), curriculum.levels().stream().map(CurriculumLevel::number).toList());
    }

    @Test void levelNumberMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> level(0));
        assertThrows(IllegalArgumentException.class, () -> level(-1));
    }

    @Test void duplicateLevelNumbersAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Curriculum(UUID.randomUUID(), List.of(level(1), level(1))));
    }

    @Test void sameGraphemeCannotBeIntroducedAtDifferentLevels() {
        UUID a = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> new Curriculum(UUID.randomUUID(), List.of(
                new CurriculumLevel(1, Set.of(a)),
                new CurriculumLevel(2, Set.of(a)))));
    }

    @Test void introducedGraphemeIdsCannotContainNull() {
        Set<UUID> idsWithNull = new java.util.HashSet<>();
        idsWithNull.add(UUID.randomUUID());
        idsWithNull.add(null);
        assertThrows(IllegalArgumentException.class, () -> new CurriculumLevel(1, idsWithNull));
    }

    @Test void emptyCurriculumIsValid() {
        Curriculum curriculum = new Curriculum(UUID.randomUUID(), List.of());
        assertTrue(curriculum.levels().isEmpty());
        assertFalse(curriculum.introductionLevel(UUID.randomUUID()).isPresent());
    }

    @Test void emptyCurriculumLevelIsValid() {
        Curriculum curriculum = new Curriculum(UUID.randomUUID(), List.of(new CurriculumLevel(1, Set.of())));
        assertEquals(1, curriculum.levels().size());
        assertTrue(curriculum.levels().get(0).introducedGraphemeIds().isEmpty());
    }

    @Test void introductionLevelReturnsConfiguredLevel() {
        UUID k = UUID.randomUUID();
        UUID i = UUID.randomUUID();
        UUID n = UUID.randomUUID();
        Curriculum curriculum = new Curriculum(UUID.randomUUID(), List.of(
                new CurriculumLevel(1, Set.of(UUID.randomUUID(), k)),
                new CurriculumLevel(2, Set.of(i, n))));

        assertEquals(1, curriculum.introductionLevel(k).orElseThrow());
        assertEquals(2, curriculum.introductionLevel(i).orElseThrow());
        assertEquals(2, curriculum.introductionLevel(n).orElseThrow());
    }

    @Test void unknownGraphemeHasNoIntroductionLevel() {
        Curriculum curriculum = new Curriculum(UUID.randomUUID(), List.of(level(1)));
        assertTrue(curriculum.introductionLevel(UUID.randomUUID()).isEmpty());
    }

    @Test void curriculumLevelsCannotContainNull() {
        List<CurriculumLevel> levels = new java.util.ArrayList<>();
        levels.add(level(1));
        levels.add(null);
        assertThrows(IllegalArgumentException.class, () -> new Curriculum(UUID.randomUUID(), levels));
    }

    @Test void introductionLevelRequiresGraphemeId() {
        Curriculum curriculum = new Curriculum(UUID.randomUUID(), List.of());
        assertThrows(NullPointerException.class, () -> curriculum.introductionLevel(null));
    }

    private CurriculumLevel level(int number) {
        return new CurriculumLevel(number, Set.of());
    }
}

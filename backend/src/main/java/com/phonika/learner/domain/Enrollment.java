package com.phonika.learner.domain;

import com.phonika.course.domain.Course;
import java.util.Objects;
import java.util.UUID;

public final class Enrollment {
    private final UUID id;
    private final Course course;
    public Enrollment(UUID id, Course course) { this.id = Objects.requireNonNull(id); this.course = Objects.requireNonNull(course); }
    public UUID id() { return id; }
    public Course course() { return course; }
}

package com.phonika.learner.domain;

import com.phonika.course.domain.Course;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Learner {
    private final UUID id;
    private final String displayName;
    private final List<Enrollment> enrollments = new ArrayList<>();
    public Learner(UUID id, String displayName) { this.id = Objects.requireNonNull(id); this.displayName = Objects.requireNonNull(displayName); }
    public Enrollment enroll(UUID enrollmentId, Course course) {
        boolean duplicate = enrollments.stream().anyMatch(e -> e.course().id().equals(course.id()));
        if (duplicate) throw new IllegalStateException("learner is already enrolled in this course");
        Enrollment enrollment = new Enrollment(enrollmentId, course); enrollments.add(enrollment); return enrollment;
    }
    public UUID id() { return id; }
    public String displayName() { return displayName; }
    public List<Enrollment> enrollments() { return Collections.unmodifiableList(enrollments); }
}

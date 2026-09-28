package com.phonika.course.application;

import com.phonika.course.domain.Course;
import java.util.List;

public final class CourseCatalog {
    private final CourseRepository repository;
    public CourseCatalog(CourseRepository repository) { this.repository = repository; }
    public List<Course> availableCourses() { return repository.findAll(); }
}

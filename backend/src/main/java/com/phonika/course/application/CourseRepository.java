package com.phonika.course.application;

import com.phonika.course.domain.Course;
import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    List<Course> findAll();
    Optional<Course> findByCode(String code);
}

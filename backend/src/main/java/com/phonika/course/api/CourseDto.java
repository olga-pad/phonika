package com.phonika.course.api;

import com.phonika.course.domain.Course;

public record CourseDto(String code, String language) {
    static CourseDto from(Course course) { return new CourseDto(course.code(), course.language().name()); }
}

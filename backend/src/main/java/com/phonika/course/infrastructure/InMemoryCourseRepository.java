package com.phonika.course.infrastructure;

import com.phonika.course.application.CourseRepository;
import com.phonika.course.domain.Course;
import com.phonika.learning.domain.Language;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public final class InMemoryCourseRepository implements CourseRepository {
    private final List<Course> courses = List.of(
        new Course(UUID.fromString("00000000-0000-0000-0000-000000000001"), "READING_RU", Language.RU, List.of()),
        new Course(UUID.fromString("00000000-0000-0000-0000-000000000002"), "READING_EN", Language.EN, List.of()),
        new Course(UUID.fromString("00000000-0000-0000-0000-000000000003"), "READING_FR", Language.FR, List.of())
    );
    public List<Course> findAll() { return courses; }
    public Optional<Course> findByCode(String code) { return courses.stream().filter(c -> c.code().equals(code)).findFirst(); }
}

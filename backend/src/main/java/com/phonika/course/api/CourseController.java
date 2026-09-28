package com.phonika.course.api;

import com.phonika.course.application.CourseCatalog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseCatalog catalog;
    public CourseController(CourseCatalog catalog) { this.catalog = catalog; }
    @GetMapping public List<CourseDto> courses() { return catalog.availableCourses().stream().map(CourseDto::from).toList(); }
}

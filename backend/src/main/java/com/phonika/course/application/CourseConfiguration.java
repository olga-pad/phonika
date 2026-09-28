package com.phonika.course.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CourseConfiguration {
    @Bean CourseCatalog courseCatalog(CourseRepository repository) { return new CourseCatalog(repository); }
}

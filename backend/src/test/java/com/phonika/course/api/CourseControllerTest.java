package com.phonika.course.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CourseControllerTest {
    @Autowired MockMvc mvc;
    @Test void returnsThreeReadingCourses() throws Exception {
        mvc.perform(get("/api/courses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].code").value("READING_RU"))
            .andExpect(jsonPath("$[0].language").value("RU"))
            .andExpect(jsonPath("$[1].language").value("EN"))
            .andExpect(jsonPath("$[2].language").value("FR"));
    }
}

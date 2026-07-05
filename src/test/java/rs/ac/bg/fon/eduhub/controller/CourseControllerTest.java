package rs.ac.bg.fon.eduhub.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import rs.ac.bg.fon.eduhub.dto.CourseDto;
import rs.ac.bg.fon.eduhub.dto.CreateCourseRequest;
import rs.ac.bg.fon.eduhub.security.JwtAuthFilter;
import rs.ac.bg.fon.eduhub.service.CourseService;

/**
 * {@code @WebMvcTest} testovi za {@link CourseController}.
 *
 * @author Mihajlo Ristanovic
 */
@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseService courseService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void testGetAllCoursesReturnsOk() throws Exception {
        CourseDto dto = new CourseDto(1L, "Java", "Opis", null, null, false, null, null, null, null, null, null, null, null);
        when(courseService.getAllCourses()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseTitle").value("Java"));
    }

    @Test
    void testGetCoursesWithKeywordCallsSearch() throws Exception {
        when(courseService.searchCourses(eq("java"), eq(null), eq(null))).thenReturn(List.of());

        mockMvc.perform(get("/api/courses").param("keyword", "java"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCourseByIdReturnsOk() throws Exception {
        CourseDto dto = new CourseDto(1L, "Java", "Opis", null, null, false, null, null, null, null, null, null, null, null);
        when(courseService.getCourseById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value(1));
    }

    @Test
    void testCreateCourseReturnsCreated() throws Exception {
        CreateCourseRequest request = new CreateCourseRequest("Java Osnove", "Opis", 1L, 1L);
        CourseDto dto = new CourseDto(1L, "Java Osnove", "Opis", null, null, false, null, null, null, null, null, null, null, null);

        when(courseService.createCourse(any(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.courseTitle").value("Java Osnove"));
    }

    @Test
    void testCreateCourseValidationFailsForBlankTitle() throws Exception {
        CreateCourseRequest invalidRequest = new CreateCourseRequest("", "Opis", 1L, 1L);

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeactivateCourseReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/courses/1"))
                .andExpect(status().isNoContent());
    }
}
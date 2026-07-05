package rs.ac.bg.fon.eduhub.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import rs.ac.bg.fon.eduhub.dto.CreateLessonRequest;
import rs.ac.bg.fon.eduhub.dto.LessonDto;
import rs.ac.bg.fon.eduhub.dto.UpdateLessonRequest;
import rs.ac.bg.fon.eduhub.security.JwtAuthFilter;
import rs.ac.bg.fon.eduhub.service.LessonService;

/**
 * {@code @WebMvcTest} testovi za {@link LessonController}.
 *
 * @author Mihajlo Ristanovic
 */
@WebMvcTest(LessonController.class)
@AutoConfigureMockMvc(addFilters = false)
class LessonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LessonService lessonService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void testGetLessonsByCourseReturnsOk() throws Exception {
        LessonDto dto = new LessonDto(1L, "Uvod", 1, null, true, 1L, 1L, "VIDEO");
        when(lessonService.getLessonsByCourse(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/courses/1/lessons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lessonTitle").value("Uvod"));
    }

    @Test
    void testAddLessonReturnsCreated() throws Exception {
        CreateLessonRequest request = new CreateLessonRequest("Uvod", 1, 1L);
        LessonDto dto = new LessonDto(1L, "Uvod", 1, null, true, 1L, 1L, "VIDEO");

        when(lessonService.addLesson(any(), any(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/courses/1/lessons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lessonTitle").value("Uvod"));
    }

    @Test
    void testAddLessonValidationFailsForBlankTitle() throws Exception {
        CreateLessonRequest invalidRequest = new CreateLessonRequest("", 1, 1L);

        mockMvc.perform(post("/api/courses/1/lessons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateLessonReturnsOk() throws Exception {
        UpdateLessonRequest request = new UpdateLessonRequest("Novi naslov", 2, 1L, false);
        LessonDto dto = new LessonDto(1L, "Novi naslov", 2, null, false, 1L, 1L, "VIDEO");

        when(lessonService.updateLesson(any(), any(), any())).thenReturn(dto);

        mockMvc.perform(put("/api/lessons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lessonTitle").value("Novi naslov"));
    }

    @Test
    void testDeleteLessonReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/lessons/1"))
                .andExpect(status().isNoContent());
    }
}
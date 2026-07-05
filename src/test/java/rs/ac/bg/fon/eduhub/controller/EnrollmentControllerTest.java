package rs.ac.bg.fon.eduhub.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import rs.ac.bg.fon.eduhub.dto.CreateEnrollmentRequest;
import rs.ac.bg.fon.eduhub.dto.EnrollmentDto;
import rs.ac.bg.fon.eduhub.dto.UpdateProgressRequest;
import rs.ac.bg.fon.eduhub.security.JwtAuthFilter;
import rs.ac.bg.fon.eduhub.service.EnrollmentService;

/**
 * {@code @WebMvcTest} testovi za {@link EnrollmentController}.
 *
 * @author Mihajlo Ristanovic
 */
@WebMvcTest(EnrollmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EnrollmentService enrollmentService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void testEnrollReturnsCreated() throws Exception {
        CreateEnrollmentRequest request = new CreateEnrollmentRequest(1L);
        EnrollmentDto dto = new EnrollmentDto(1L, null, 0, null, 1L, "Petar Nikolic", 1L, "Java", 1L, "ACTIVE");

        when(enrollmentService.enroll(any(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.enrollmentStatusName").value("ACTIVE"));
    }

    @Test
    void testGetMyEnrollmentsReturnsOk() throws Exception {
        EnrollmentDto dto = new EnrollmentDto(1L, null, 0, null, 1L, "Petar Nikolic", 1L, "Java", 1L, "ACTIVE");
        when(enrollmentService.getMyEnrollments(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/enrollments/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseTitle").value("Java"));
    }

    @Test
    void testUpdateProgressReturnsOk() throws Exception {
        UpdateProgressRequest request = new UpdateProgressRequest(50);
        EnrollmentDto dto = new EnrollmentDto(1L, null, 50, null, 1L, "Petar Nikolic", 1L, "Java", 1L, "ACTIVE");

        when(enrollmentService.updateProgress(any(), any(), any())).thenReturn(dto);

        mockMvc.perform(patch("/api/enrollments/1/progress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progressPercentage").value(50));
    }

    @Test
    void testUpdateProgressValidationFailsForOutOfRangeValue() throws Exception {
        UpdateProgressRequest invalidRequest = new UpdateProgressRequest(150);

        mockMvc.perform(patch("/api/enrollments/1/progress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
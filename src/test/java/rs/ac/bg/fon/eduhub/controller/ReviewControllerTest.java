package rs.ac.bg.fon.eduhub.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import rs.ac.bg.fon.eduhub.dto.CreateReviewRequest;
import rs.ac.bg.fon.eduhub.dto.ReviewDto;
import rs.ac.bg.fon.eduhub.security.JwtAuthFilter;
import rs.ac.bg.fon.eduhub.service.ReviewService;

/**
 * {@code @WebMvcTest} testovi za {@link ReviewController}.
 *
 * @author Mihajlo Ristanovic
 */
@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void testAddReviewReturnsCreated() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest(5, "Odlicno");
        ReviewDto dto = new ReviewDto(1L, 5, "Odlicno", null, 1L, 1L, "Java", 1L, "Petar Nikolic");

        when(reviewService.addReview(any(), any(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/enrollments/1/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    void testAddReviewValidationFailsForOutOfRangeRating() throws Exception {
        CreateReviewRequest invalidRequest = new CreateReviewRequest(6, "Odlicno");

        mockMvc.perform(post("/api/enrollments/1/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetReviewsByCourseReturnsOk() throws Exception {
        ReviewDto dto = new ReviewDto(1L, 5, "Odlicno", null, 1L, 1L, "Java", 1L, "Petar Nikolic");
        when(reviewService.getReviewsByCourse(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/courses/1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rating").value(5));
    }
}
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
import rs.ac.bg.fon.eduhub.dto.CreateMaterialRequest;
import rs.ac.bg.fon.eduhub.dto.MaterialDto;
import rs.ac.bg.fon.eduhub.security.JwtAuthFilter;
import rs.ac.bg.fon.eduhub.service.MaterialService;

/**
 * {@code @WebMvcTest} testovi za {@link MaterialController}.
 *
 * @author Mihajlo Ristanovic
 */
@WebMvcTest(MaterialController.class)
@AutoConfigureMockMvc(addFilters = false)
class MaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MaterialService materialService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void testGetMaterialsByLessonReturnsOk() throws Exception {
        MaterialDto dto = new MaterialDto(1L, "Slajdovi", 1, null, "http://x.com", null, 1L, 1L, "PDF");
        when(materialService.getMaterialsByLesson(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/lessons/1/materials"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].materialName").value("Slajdovi"));
    }

    @Test
    void testAddMaterialReturnsCreated() throws Exception {
        CreateMaterialRequest request = new CreateMaterialRequest("Slajdovi", 1, null, "http://x.com", 1L);
        MaterialDto dto = new MaterialDto(1L, "Slajdovi", 1, null, "http://x.com", null, 1L, 1L, "PDF");

        when(materialService.addMaterial(any(), any(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/lessons/1/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.materialName").value("Slajdovi"));
    }

    @Test
    void testAddMaterialValidationFailsForBlankName() throws Exception {
        CreateMaterialRequest invalidRequest = new CreateMaterialRequest("", 1, null, "http://x.com", 1L);

        mockMvc.perform(post("/api/lessons/1/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
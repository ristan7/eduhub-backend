package rs.ac.bg.fon.eduhub.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import rs.ac.bg.fon.eduhub.dto.CertificateDto;
import rs.ac.bg.fon.eduhub.security.JwtAuthFilter;
import rs.ac.bg.fon.eduhub.service.CertificateService;

/**
 * {@code @WebMvcTest} testovi za {@link CertificateController}.
 *
 * @author Mihajlo Ristanovic
 */
@WebMvcTest(CertificateController.class)
@AutoConfigureMockMvc(addFilters = false)
class CertificateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CertificateService certificateService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void testIssueCertificateReturnsCreated() throws Exception {
        CertificateDto dto = new CertificateDto(1L, "CERT-ABCD1234-2026", null, null, 1L, 1L, "Java", 1L, "Petar Nikolic");
        when(certificateService.issueCertificate(any(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/enrollments/1/certificate"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("CERT-ABCD1234-2026"));
    }

    @Test
    void testGetMyCertificatesReturnsOk() throws Exception {
        CertificateDto dto = new CertificateDto(1L, "CERT-ABCD1234-2026", null, null, 1L, 1L, "Java", 1L, "Petar Nikolic");
        when(certificateService.getMyCertificates(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/certificates/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("CERT-ABCD1234-2026"));
    }
}
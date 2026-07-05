package rs.ac.bg.fon.eduhub.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import rs.ac.bg.fon.eduhub.dto.AuthResponse;
import rs.ac.bg.fon.eduhub.dto.LoginRequest;
import rs.ac.bg.fon.eduhub.dto.RegisterRequest;
import rs.ac.bg.fon.eduhub.dto.UserDto;
import rs.ac.bg.fon.eduhub.security.JwtAuthFilter;
import rs.ac.bg.fon.eduhub.service.AuthService;

/**
 * {@code @WebMvcTest} testovi za {@link AuthController} — proveravaju HTTP
 * sloj (mapiranje ruta, statusne kodove, validaciju, JSON) uz mokovan
 * servisni sloj.
 *
 * @author Mihajlo Ristanovic
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void testRegisterReturnsCreated() throws Exception {
        RegisterRequest request = new RegisterRequest("student@eduhub.com", "password123", "Petar", "Nikolic");
        UserDto responseDto = new UserDto(1L, "student@eduhub.com", "Petar", "Nikolic", "STUDENT", true, null);

        when(authService.register(any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userEmail").value("student@eduhub.com"))
                .andExpect(jsonPath("$.roleName").value("STUDENT"));
    }

    @Test
    void testRegisterValidationFailsForBlankEmail() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest("", "password123", "Petar", "Nikolic");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginReturnsToken() throws Exception {
        LoginRequest request = new LoginRequest("student@eduhub.com", "password123");
        UserDto userDto = new UserDto(1L, "student@eduhub.com", "Petar", "Nikolic", "STUDENT", true, null);
        AuthResponse response = new AuthResponse("jwt-token-123", userDto);

        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.user.userEmail").value("student@eduhub.com"));
    }

    @Test
    void testLogoutReturnsOk() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk());
    }
}
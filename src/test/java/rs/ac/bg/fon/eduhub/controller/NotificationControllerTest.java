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
import rs.ac.bg.fon.eduhub.dto.CreateNotificationRequest;
import rs.ac.bg.fon.eduhub.dto.NotificationDto;
import rs.ac.bg.fon.eduhub.security.JwtAuthFilter;
import rs.ac.bg.fon.eduhub.service.NotificationService;

/**
 * {@code @WebMvcTest} testovi za {@link NotificationController}.
 *
 * @author Mihajlo Ristanovic
 */
@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void testSendNotificationReturnsCreated() throws Exception {
        CreateNotificationRequest request = new CreateNotificationRequest(1L, "Naslov", "Poruka", 1L);
        NotificationDto dto = new NotificationDto(1L, "Naslov", "Poruka", null, false, 1L, 1L, "SYSTEM");

        when(notificationService.sendNotification(any())).thenReturn(dto);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notificationTitle").value("Naslov"));
    }

    @Test
    void testSendNotificationValidationFailsForBlankMessage() throws Exception {
        CreateNotificationRequest invalidRequest = new CreateNotificationRequest(1L, "Naslov", "", 1L);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetMyNotificationsReturnsOk() throws Exception {
        NotificationDto dto = new NotificationDto(1L, "Naslov", "Poruka", null, false, 1L, 1L, "SYSTEM");
        when(notificationService.getMyNotifications(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/notifications/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].notificationTitle").value("Naslov"));
    }

    @Test
    void testMarkAsReadReturnsOk() throws Exception {
        NotificationDto dto = new NotificationDto(1L, "Naslov", "Poruka", null, true, 1L, 1L, "SYSTEM");
        when(notificationService.markAsRead(any(), any())).thenReturn(dto);

        mockMvc.perform(patch("/api/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isRead").value(true));
    }
}
package rs.ac.bg.fon.eduhub.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import rs.ac.bg.fon.eduhub.dto.CourseDto;
import rs.ac.bg.fon.eduhub.dto.PlatformStatisticsDto;
import rs.ac.bg.fon.eduhub.dto.UpdateUserRoleRequest;
import rs.ac.bg.fon.eduhub.dto.UpdateUserStatusRequest;
import rs.ac.bg.fon.eduhub.dto.UserDto;
import rs.ac.bg.fon.eduhub.security.JwtAuthFilter;
import rs.ac.bg.fon.eduhub.service.AdminService;

/**
 * {@code @WebMvcTest} testovi za {@link AdminController}.
 *
 * @author Mihajlo Ristanovic
 */
@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void testGetAllUsersReturnsOk() throws Exception {
        UserDto dto = new UserDto(1L, "user@eduhub.com", "Petar", "Nikolic", "STUDENT", true, null);
        when(adminService.getAllUsers()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userEmail").value("user@eduhub.com"));
    }

    @Test
    void testSetUserStatusReturnsOk() throws Exception {
        UpdateUserStatusRequest request = new UpdateUserStatusRequest(false);
        UserDto dto = new UserDto(1L, "user@eduhub.com", "Petar", "Nikolic", "STUDENT", false, null);

        when(adminService.setUserActiveStatus(1L, false)).thenReturn(dto);

        mockMvc.perform(patch("/api/admin/users/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void testUpdateUserRoleReturnsOk() throws Exception {
        UpdateUserRoleRequest request = new UpdateUserRoleRequest(2L);
        UserDto dto = new UserDto(1L, "user@eduhub.com", "Petar", "Nikolic", "INSTRUCTOR", true, null);

        when(adminService.updateUserRole(1L, 2L)).thenReturn(dto);

        mockMvc.perform(patch("/api/admin/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleName").value("INSTRUCTOR"));
    }

    @Test
    void testApproveCourseReturnsOk() throws Exception {
        CourseDto dto = new CourseDto(1L, "Java", "Opis", null, null, true, null, null, null, null, null, null, 2L, "PUBLISHED");
        when(adminService.approveCourse(1L)).thenReturn(dto);

        mockMvc.perform(patch("/api/admin/courses/1/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseStatusName").value("PUBLISHED"));
    }

    @Test
    void testGetStatisticsReturnsOk() throws Exception {
        PlatformStatisticsDto dto = new PlatformStatisticsDto(10, 7, 2, 1, 5, 3, 1, 1, 20, 4);
        when(adminService.getPlatformStatistics()).thenReturn(dto);

        mockMvc.perform(get("/api/admin/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(10));
    }
}
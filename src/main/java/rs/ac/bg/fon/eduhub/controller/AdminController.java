package rs.ac.bg.fon.eduhub.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.bg.fon.eduhub.dto.CourseDto;
import rs.ac.bg.fon.eduhub.dto.PlatformStatisticsDto;
import rs.ac.bg.fon.eduhub.dto.UpdateUserRoleRequest;
import rs.ac.bg.fon.eduhub.dto.UpdateUserStatusRequest;
import rs.ac.bg.fon.eduhub.dto.UserDto;
import rs.ac.bg.fon.eduhub.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // SO21 - Upravljanje korisnicima
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<UserDto> setUserStatus(@PathVariable Long id,
                                                 @Valid @RequestBody UpdateUserStatusRequest request) {
        return ResponseEntity.ok(adminService.setUserActiveStatus(id, request.isActive()));
    }

    // SO22 - Dodela uloga korisnicima
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<UserDto> updateUserRole(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateUserRoleRequest request) {
        return ResponseEntity.ok(adminService.updateUserRole(id, request.roleId()));
    }

    // SO23 - Odobravanje ili blokiranje kursa
    @PatchMapping("/courses/{id}/approve")
    public ResponseEntity<CourseDto> approveCourse(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approveCourse(id));
    }

    @PatchMapping("/courses/{id}/block")
    public ResponseEntity<CourseDto> blockCourse(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.blockCourse(id));
    }

    // SO24 - Pregled statistike platforme
    @GetMapping("/statistics")
    public ResponseEntity<PlatformStatisticsDto> getStatistics() {
        return ResponseEntity.ok(adminService.getPlatformStatistics());
    }
}
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

/**
 * REST kontroler za administratorske operacije: upravljanje
 * korisnicima, dodelu uloga, odobravanje/blokiranje kurseva i pregled
 * statistike platforme (SO21-SO24). Svi endpointi ovog kontrolera
 * zahtevaju ulogu ADMIN.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    /**
     * Kreira kontroler sa injektovanim servisom za administratorske operacije.
     *
     * @param adminService servis koji implementira administratorsku poslovnu logiku
     */
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Vraća listu svih registrovanih korisnika (SO21).
     *
     * @return HTTP 200 sa listom svih korisnika
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * Vraća detalje jednog korisnika po identifikatoru (SO21).
     *
     * @param id identifikator korisnika
     * @return HTTP 200 sa detaljima korisnika
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    /**
     * Menja status aktivnosti naloga korisnika (SO21).
     *
     * @param id identifikator korisnika
     * @param request nova vrednost statusa aktivnosti naloga
     * @return HTTP 200 sa podacima ažuriranog korisnika
     */
    @PatchMapping("/users/{id}/status")
    public ResponseEntity<UserDto> setUserStatus(@PathVariable Long id,
                                                 @Valid @RequestBody UpdateUserStatusRequest request) {
        return ResponseEntity.ok(adminService.setUserActiveStatus(id, request.isActive()));
    }

    /**
     * Dodeljuje novu ulogu korisniku (SO22).
     *
     * @param id identifikator korisnika
     * @param request identifikator nove uloge
     * @return HTTP 200 sa podacima ažuriranog korisnika
     */
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<UserDto> updateUserRole(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateUserRoleRequest request) {
        return ResponseEntity.ok(adminService.updateUserRole(id, request.roleId()));
    }

    /**
     * Odobrava kurs, postavljajući njegov status na PUBLISHED (SO23).
     *
     * @param id identifikator kursa koji se odobrava
     * @return HTTP 200 sa podacima ažuriranog kursa
     */
    @PatchMapping("/courses/{id}/approve")
    public ResponseEntity<CourseDto> approveCourse(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approveCourse(id));
    }

    /**
     * Blokira kurs, postavljajući njegov status na ARCHIVED (SO23).
     *
     * @param id identifikator kursa koji se blokira
     * @return HTTP 200 sa podacima ažuriranog kursa
     */
    @PatchMapping("/courses/{id}/block")
    public ResponseEntity<CourseDto> blockCourse(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.blockCourse(id));
    }

    /**
     * Vraća zbirnu statistiku platforme (SO24).
     *
     * @return HTTP 200 sa statistikom platforme
     */
    @GetMapping("/statistics")
    public ResponseEntity<PlatformStatisticsDto> getStatistics() {
        return ResponseEntity.ok(adminService.getPlatformStatistics());
    }
}
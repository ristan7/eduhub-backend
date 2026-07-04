package rs.ac.bg.fon.eduhub.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.bg.fon.eduhub.dto.CreateEnrollmentRequest;
import rs.ac.bg.fon.eduhub.dto.EnrollmentDto;
import rs.ac.bg.fon.eduhub.dto.UpdateProgressRequest;
import rs.ac.bg.fon.eduhub.service.EnrollmentService;

/**
 * REST kontroler za prijavu studenata na kurseve, pregled prijava i
 * praćenje napretka (SO10, SO11, SO18).
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    /**
     * Kreira kontroler sa injektovanim servisom za prijave.
     *
     * @param enrollmentService servis koji implementira poslovnu logiku prijava
     */
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    /**
     * Prijavljuje trenutno prijavljenog korisnika (studenta) na zadati
     * kurs (SO10).
     *
     * @param request podaci o kursu na koji se student prijavljuje
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return HTTP 201 sa podacima novokreirane prijave
     */
    @PostMapping
    public ResponseEntity<EnrollmentDto> enroll(@Valid @RequestBody CreateEnrollmentRequest request,
                                                Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.enroll(request, authentication));
    }

    /**
     * Vraća listu svih prijava trenutno prijavljenog korisnika (SO11).
     *
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return HTTP 200 sa listom prijava korisnika
     */
    @GetMapping("/me")
    public ResponseEntity<List<EnrollmentDto>> getMyEnrollments(Authentication authentication) {
        return ResponseEntity.ok(enrollmentService.getMyEnrollments(authentication));
    }

    /**
     * Ažurira procenat napretka studenta kroz kurs (SO18).
     *
     * @param id identifikator prijave čiji se napredak ažurira
     * @param request novi procenat napretka
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return HTTP 200 sa podacima ažurirane prijave
     */
    @PatchMapping("/{id}/progress")
    public ResponseEntity<EnrollmentDto> updateProgress(@PathVariable Long id,
                                                        @Valid @RequestBody UpdateProgressRequest request,
                                                        Authentication authentication) {
        return ResponseEntity.ok(enrollmentService.updateProgress(id, request, authentication));
    }
}
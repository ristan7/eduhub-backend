package rs.ac.bg.fon.eduhub.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.bg.fon.eduhub.dto.CreateEnrollmentRequest;
import rs.ac.bg.fon.eduhub.dto.EnrollmentDto;
import rs.ac.bg.fon.eduhub.service.EnrollmentService;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import rs.ac.bg.fon.eduhub.dto.UpdateProgressRequest;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // SO10 - Prijava studenta na kurs
    @PostMapping
    public ResponseEntity<EnrollmentDto> enroll(@Valid @RequestBody CreateEnrollmentRequest request,
                                                Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.enroll(request, authentication));
    }

    // SO11 - Pregled prijavljenih kurseva studenta
    @GetMapping("/me")
    public ResponseEntity<List<EnrollmentDto>> getMyEnrollments(Authentication authentication) {
        return ResponseEntity.ok(enrollmentService.getMyEnrollments(authentication));
    }

    // SO18 - Praćenje napretka studenta
    @PatchMapping("/{id}/progress")
    public ResponseEntity<EnrollmentDto> updateProgress(@PathVariable Long id,
                                                        @Valid @RequestBody UpdateProgressRequest request,
                                                        Authentication authentication) {
        return ResponseEntity.ok(enrollmentService.updateProgress(id, request, authentication));
    }
}
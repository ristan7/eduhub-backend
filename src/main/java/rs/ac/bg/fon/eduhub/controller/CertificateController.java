package rs.ac.bg.fon.eduhub.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.bg.fon.eduhub.dto.CertificateDto;
import rs.ac.bg.fon.eduhub.service.CertificateService;

@RestController
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    // SO28 - Izdavanje sertifikata studentu
    @PostMapping("/api/enrollments/{enrollmentId}/certificate")
    public ResponseEntity<CertificateDto> issueCertificate(@PathVariable Long enrollmentId, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(certificateService.issueCertificate(enrollmentId, authentication));
    }

    // SO29 - Pregled sertifikata studenta
    @GetMapping("/api/certificates/me")
    public ResponseEntity<List<CertificateDto>> getMyCertificates(Authentication authentication) {
        return ResponseEntity.ok(certificateService.getMyCertificates(authentication));
    }
}
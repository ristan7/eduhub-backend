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

/**
 * REST kontroler za izdavanje sertifikata i pregled sertifikata
 * studenta (SO28, SO29).
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@RestController
public class CertificateController {

    private final CertificateService certificateService;

    /**
     * Kreira kontroler sa injektovanim servisom za sertifikate.
     *
     * @param certificateService servis koji implementira poslovnu logiku sertifikata
     */
    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    /**
     * Izdaje sertifikat za zadatu prijavu (SO28).
     *
     * @param enrollmentId identifikator prijave za koju se sertifikat izdaje
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return HTTP 201 sa podacima novoizdatog sertifikata
     */
    @PostMapping("/api/enrollments/{enrollmentId}/certificate")
    public ResponseEntity<CertificateDto> issueCertificate(@PathVariable Long enrollmentId, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(certificateService.issueCertificate(enrollmentId, authentication));
    }

    /**
     * Vraća listu svih sertifikata trenutno prijavljenog korisnika
     * (SO29).
     *
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return HTTP 200 sa listom sertifikata korisnika
     */
    @GetMapping("/api/certificates/me")
    public ResponseEntity<List<CertificateDto>> getMyCertificates(Authentication authentication) {
        return ResponseEntity.ok(certificateService.getMyCertificates(authentication));
    }
}
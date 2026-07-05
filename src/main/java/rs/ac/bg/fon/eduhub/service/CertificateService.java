package rs.ac.bg.fon.eduhub.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.eduhub.dto.CertificateDto;
import rs.ac.bg.fon.eduhub.entity.impl.Certificate;
import rs.ac.bg.fon.eduhub.entity.impl.Enrollment;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.mapper.CertificateMapper;
import rs.ac.bg.fon.eduhub.repository.CertificateRepository;
import rs.ac.bg.fon.eduhub.repository.EnrollmentRepository;
import rs.ac.bg.fon.eduhub.repository.UserRepository;

/**
 * Servis koji implementira poslovnu logiku izdavanja sertifikata i
 * pregleda sertifikata studenta (SO28, SO29). Izdavanje sertifikata
 * dozvoljeno je samo autoru kursa kojem prijava pripada ili korisniku
 * sa ulogom ADMIN, i to najviše jednom po prijavi.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CertificateMapper certificateMapper;

    /**
     * Kreira servis sa svim potrebnim zavisnostima.
     *
     * @param certificateRepository repozitorijum sertifikata
     * @param enrollmentRepository repozitorijum prijava
     * @param userRepository repozitorijum korisnika
     * @param certificateMapper mapper za konverziju entiteta sertifikata u DTO
     */
    public CertificateService(CertificateRepository certificateRepository,
                              EnrollmentRepository enrollmentRepository,
                              UserRepository userRepository,
                              CertificateMapper certificateMapper) {
        this.certificateRepository = certificateRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.certificateMapper = certificateMapper;
    }

    /**
     * Izdaje sertifikat za zadatu prijavu, sa automatski generisanim
     * jedinstvenim kodom (SO28). Dozvoljeno samo autoru kursa kojem
     * prijava pripada ili korisniku sa ulogom ADMIN, i to najviše
     * jednom po prijavi.
     *
     * @param enrollmentId identifikator prijave za koju se sertifikat izdaje
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return DTO novoizdatog sertifikata
     * @throws IllegalArgumentException ako prijava sa datim identifikatorom ne postoji, ili već ima sertifikat
     * @throws AccessDeniedException ako korisnik nije autor kursa niti ima ulogu ADMIN
     */
    public CertificateDto issueCertificate(Long enrollmentId, Authentication authentication) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found: " + enrollmentId));

        requireCourseOwnerOrAdmin(enrollment, authentication);

        certificateRepository.findByEnrollment_EnrollmentId(enrollmentId).ifPresent(c -> {
            throw new IllegalArgumentException("Certificate already issued for this enrollment.");
        });

        Certificate certificate = new Certificate();
        certificate.setCode(generateCertificateCode());
        certificate.setEnrollment(enrollment);
        certificate.setCertificateUrl(null);

        certificateRepository.save(certificate);
        return certificateMapper.toDto(certificate);
    }

    /**
     * Vraća listu svih sertifikata trenutno prijavljenog korisnika (SO29).
     *
     * @param authentication autentifikacija trenutno prijavljenog korisnika (student)
     * @return lista sertifikata korisnika
     */
    public List<CertificateDto> getMyCertificates(Authentication authentication) {
        User student = currentUser(authentication);
        return certificateRepository.findByEnrollment_Student_UserId(student.getUserId()).stream()
                .map(certificateMapper::toDto)
                .toList();
    }

    /**
     * Generiše jedinstveni kod sertifikata na osnovu nasumičnog UUID-a i
     * tekuće godine.
     *
     * @return generisani kod sertifikata, u formatu "CERT-XXXXXXXX-GGGG"
     */
    private String generateCertificateCode() {
        return "CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + "-" + LocalDateTime.now().getYear();
    }

    /**
     * Pronalazi entitet korisnika na osnovu email adrese iz autentifikacije.
     *
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return pronađeni entitet korisnika
     * @throws IllegalStateException ako autentifikovani korisnik neočekivano ne postoji u bazi
     */
    private User currentUser(Authentication authentication) {
        return userRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));
    }

    /**
     * Proverava da li trenutno prijavljeni korisnik ima pravo da izda
     * sertifikat za datu prijavu (mora biti autor kursa ili imati ulogu
     * ADMIN).
     *
     * @param enrollment prijava za koju se sertifikat izdaje
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @throws AccessDeniedException ako korisnik nije autor kursa niti ima ulogu ADMIN
     */
    private void requireCourseOwnerOrAdmin(Enrollment enrollment, Authentication authentication) {
        User currentUser = currentUser(authentication);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = enrollment.getCourse() != null
                && enrollment.getCourse().getAuthor() != null
                && enrollment.getCourse().getAuthor().getUserId().equals(currentUser.getUserId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Only the course author or an admin can issue certificates.");
        }
    }
}
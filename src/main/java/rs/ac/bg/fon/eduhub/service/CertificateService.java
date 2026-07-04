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

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CertificateMapper certificateMapper;

    public CertificateService(CertificateRepository certificateRepository,
                              EnrollmentRepository enrollmentRepository,
                              UserRepository userRepository,
                              CertificateMapper certificateMapper) {
        this.certificateRepository = certificateRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.certificateMapper = certificateMapper;
    }

    // SO28 - Izdavanje sertifikata studentu
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

    // SO29 - Pregled sertifikata studenta
    public List<CertificateDto> getMyCertificates(Authentication authentication) {
        User student = currentUser(authentication);
        return certificateRepository.findByEnrollment_Student_UserId(student.getUserId()).stream()
                .map(certificateMapper::toDto)
                .toList();
    }

    private String generateCertificateCode() {
        return "CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + "-" + LocalDateTime.now().getYear();
    }

    private User currentUser(Authentication authentication) {
        return userRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));
    }

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
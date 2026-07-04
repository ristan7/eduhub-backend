package rs.ac.bg.fon.eduhub.mapper;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.eduhub.dto.CertificateDto;
import rs.ac.bg.fon.eduhub.entity.impl.Certificate;

@Component
public class CertificateMapper {

    public CertificateDto toDto(Certificate certificate) {
        if (certificate == null) {
            return null;
        }
        var enrollment = certificate.getEnrollment();
        return new CertificateDto(
                certificate.getCertificateId(),
                certificate.getCode(),
                certificate.getIssuedAt(),
                certificate.getCertificateUrl(),
                enrollment != null ? enrollment.getEnrollmentId() : null,
                enrollment != null && enrollment.getCourse() != null ? enrollment.getCourse().getCourseId() : null,
                enrollment != null && enrollment.getCourse() != null ? enrollment.getCourse().getCourseTitle() : null,
                enrollment != null && enrollment.getStudent() != null ? enrollment.getStudent().getUserId() : null,
                enrollment != null && enrollment.getStudent() != null
                        ? enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName()
                        : null
        );
    }
}
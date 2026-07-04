package rs.ac.bg.fon.eduhub.mapper;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.eduhub.dto.CertificateDto;
import rs.ac.bg.fon.eduhub.entity.impl.Certificate;

/**
 * Konvertuje {@link Certificate} entitete u odgovarajuće {@link CertificateDto} objekte.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Component
public class CertificateMapper {

    /**
     * Mapira entitet sertifikata u DTO za slanje klijentu, uz podatke o
     * kursu i studentu preuzete preko povezane prijave ({@code enrollment}).
     *
     * @param certificate entitet sertifikata, može biti {@code null}
     * @return {@link CertificateDto} sa podacima sertifikata, ili {@code null} ako je ulazni entitet {@code null}
     */
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
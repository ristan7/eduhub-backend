package rs.ac.bg.fon.eduhub.mapper;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.eduhub.dto.EnrollmentDto;
import rs.ac.bg.fon.eduhub.entity.impl.Enrollment;

/**
 * Konvertuje {@link Enrollment} entitete u odgovarajuće {@link EnrollmentDto} objekte.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Component
public class EnrollmentMapper {

    /**
     * Mapira entitet prijave u DTO za slanje klijentu.
     *
     * @param enrollment entitet prijave, može biti {@code null}
     * @return {@link EnrollmentDto} sa podacima prijave, ili {@code null} ako je ulazni entitet {@code null}
     */
    public EnrollmentDto toDto(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }
        return new EnrollmentDto(
                enrollment.getEnrollmentId(),
                enrollment.getEnrolledAt(),
                enrollment.getProgressPercentage(),
                enrollment.getCompletedAt(),
                enrollment.getStudent() != null ? enrollment.getStudent().getUserId() : null,
                enrollment.getStudent() != null
                        ? enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName()
                        : null,
                enrollment.getCourse() != null ? enrollment.getCourse().getCourseId() : null,
                enrollment.getCourse() != null ? enrollment.getCourse().getCourseTitle() : null,
                enrollment.getEnrollmentStatus() != null ? enrollment.getEnrollmentStatus().getEnrollmentStatusId() : null,
                enrollment.getEnrollmentStatus() != null ? enrollment.getEnrollmentStatus().getEnrollmentStatusName() : null
        );
    }
}
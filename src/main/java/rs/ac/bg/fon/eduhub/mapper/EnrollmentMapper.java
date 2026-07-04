package rs.ac.bg.fon.eduhub.mapper;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.eduhub.dto.EnrollmentDto;
import rs.ac.bg.fon.eduhub.entity.impl.Enrollment;

@Component
public class EnrollmentMapper {

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
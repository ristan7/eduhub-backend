package rs.ac.bg.fon.eduhub.dto;

import java.time.LocalDateTime;

public record EnrollmentDto(
        Long enrollmentId,
        LocalDateTime enrolledAt,
        Integer progressPercentage,
        LocalDateTime completedAt,
        Long studentId,
        String studentFullName,
        Long courseId,
        String courseTitle,
        Long enrollmentStatusId,
        String enrollmentStatusName
) {
}
package rs.ac.bg.fon.eduhub.dto;

import java.time.LocalDateTime;

public record ReviewDto(
        Long reviewId,
        Integer rating,
        String comment,
        LocalDateTime createdAt,
        Long enrollmentId,
        Long courseId,
        String courseTitle,
        Long studentId,
        String studentFullName
) {
}
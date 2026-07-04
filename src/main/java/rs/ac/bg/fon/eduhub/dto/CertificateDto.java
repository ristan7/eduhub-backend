package rs.ac.bg.fon.eduhub.dto;

import java.time.LocalDateTime;

public record CertificateDto(
        Long certificateId,
        String code,
        LocalDateTime issuedAt,
        String certificateUrl,
        Long enrollmentId,
        Long courseId,
        String courseTitle,
        Long studentId,
        String studentFullName
) {
}
package rs.ac.bg.fon.eduhub.dto;

import java.time.LocalDateTime;

public record CourseDto(
        Long courseId,
        String courseTitle,
        String courseDescription,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean isPublished,
        Long authorId,
        String authorFullName,
        Long courseCategoryId,
        String courseCategoryName,
        Long courseLevelId,
        String courseLevelName,
        Long courseStatusId,
        String courseStatusName
) {
}
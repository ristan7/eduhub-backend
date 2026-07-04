package rs.ac.bg.fon.eduhub.dto;

import java.time.LocalDateTime;

public record LessonDto(
        Long lessonId,
        String lessonTitle,
        Integer lessonOrderIndex,
        LocalDateTime createdAt,
        Boolean isAvailable,
        Long courseId,
        Long lessonTypeId,
        String lessonTypeName
) {
}
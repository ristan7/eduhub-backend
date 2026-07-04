package rs.ac.bg.fon.eduhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateLessonRequest(
        @NotBlank @Size(max = 150) String lessonTitle,
        @NotNull Integer lessonOrderIndex,
        @NotNull Long lessonTypeId,
        @NotNull Boolean isAvailable
) {
}
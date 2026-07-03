package rs.ac.bg.fon.eduhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCourseRequest(
        @NotBlank @Size(max = 150) String courseTitle,
        @NotBlank String courseDescription,
        @NotNull Long courseCategoryId,
        @NotNull Long courseLevelId
) {
}
package rs.ac.bg.fon.eduhub.dto;

import jakarta.validation.constraints.NotNull;

public record CreateEnrollmentRequest(
        @NotNull Long courseId
) {
}
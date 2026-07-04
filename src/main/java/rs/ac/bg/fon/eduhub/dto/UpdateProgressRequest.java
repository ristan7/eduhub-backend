package rs.ac.bg.fon.eduhub.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateProgressRequest(
        @NotNull @Min(0) @Max(100) Integer progressPercentage
) {
}
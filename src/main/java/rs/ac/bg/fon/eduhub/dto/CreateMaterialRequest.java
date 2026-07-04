package rs.ac.bg.fon.eduhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateMaterialRequest(
        @NotBlank @Size(max = 150) String materialName,
        @NotNull Integer materialOrderIndex,
        String content,
        @Size(max = 500) String url,
        @NotNull Long materialTypeId
) {
}
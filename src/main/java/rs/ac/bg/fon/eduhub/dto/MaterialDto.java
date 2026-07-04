package rs.ac.bg.fon.eduhub.dto;

import java.time.LocalDateTime;

public record MaterialDto(
        Long materialId,
        String materialName,
        Integer materialOrderIndex,
        String content,
        String url,
        LocalDateTime uploadedAt,
        Long lessonId,
        Long materialTypeId,
        String materialTypeName
) {
}
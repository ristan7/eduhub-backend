package rs.ac.bg.fon.eduhub.mapper;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.eduhub.dto.MaterialDto;
import rs.ac.bg.fon.eduhub.entity.impl.Material;

@Component
public class MaterialMapper {

    public MaterialDto toDto(Material material) {
        if (material == null) {
            return null;
        }
        return new MaterialDto(
                material.getMaterialId(),
                material.getMaterialName(),
                material.getMaterialOrderIndex(),
                material.getContent(),
                material.getUrl(),
                material.getUploadedAt(),
                material.getLesson() != null ? material.getLesson().getLessonId() : null,
                material.getMaterialType() != null ? material.getMaterialType().getMaterialTypeId() : null,
                material.getMaterialType() != null ? material.getMaterialType().getMaterialTypeName() : null
        );
    }
}
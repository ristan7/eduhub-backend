package rs.ac.bg.fon.eduhub.mapper;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.eduhub.dto.MaterialDto;
import rs.ac.bg.fon.eduhub.entity.impl.Material;

/**
 * Konvertuje {@link Material} entitete u odgovarajuće {@link MaterialDto} objekte.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Component
public class MaterialMapper {

    /**
     * Mapira entitet nastavnog materijala u DTO za slanje klijentu.
     *
     * @param material entitet materijala, može biti {@code null}
     * @return {@link MaterialDto} sa podacima materijala, ili {@code null} ako je ulazni entitet {@code null}
     */
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
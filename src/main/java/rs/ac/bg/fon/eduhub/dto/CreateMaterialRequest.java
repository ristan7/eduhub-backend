package rs.ac.bg.fon.eduhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Zahtev za dodavanje novog nastavnog materijala na lekciju (SO16).
 *
 * @param materialName naziv materijala
 * @param materialOrderIndex redni broj materijala u okviru lekcije
 * @param content tekstualni sadržaj materijala, opciono
 * @param url eksterna URL adresa materijala, opciono
 * @param materialTypeId identifikator tipa materijala
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record CreateMaterialRequest(
        @NotBlank @Size(max = 150) String materialName,
        @NotNull Integer materialOrderIndex,
        String content,
        @Size(max = 500) String url,
        @NotNull Long materialTypeId
) {
}
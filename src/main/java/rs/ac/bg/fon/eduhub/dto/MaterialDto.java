package rs.ac.bg.fon.eduhub.dto;

import java.time.LocalDateTime;

/**
 * Prenosni objekat (DTO) koji predstavlja podatke o nastavnom
 * materijalu vraćene klijentu.
 *
 * @param materialId jedinstveni identifikator materijala
 * @param materialName naziv materijala
 * @param materialOrderIndex redni broj materijala u okviru lekcije
 * @param content tekstualni sadržaj materijala, ako postoji
 * @param url eksterna URL adresa materijala, ako postoji
 * @param uploadedAt datum i vreme dodavanja materijala
 * @param lessonId identifikator lekcije kojoj materijal pripada
 * @param materialTypeId identifikator tipa materijala
 * @param materialTypeName naziv tipa materijala
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
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
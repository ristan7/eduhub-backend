package rs.ac.bg.fon.eduhub.dto;

import java.time.LocalDateTime;

/**
 * Prenosni objekat (DTO) koji predstavlja podatke o lekciji vraćene
 * klijentu.
 *
 * @param lessonId jedinstveni identifikator lekcije
 * @param lessonTitle naslov lekcije
 * @param lessonOrderIndex redni broj lekcije u okviru kursa
 * @param createdAt datum i vreme kreiranja lekcije
 * @param isAvailable da li je lekcija trenutno dostupna studentima
 * @param courseId identifikator kursa kojem lekcija pripada
 * @param lessonTypeId identifikator tipa lekcije
 * @param lessonTypeName naziv tipa lekcije
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record LessonDto(
        Long lessonId,
        String lessonTitle,
        Integer lessonOrderIndex,
        LocalDateTime createdAt,
        Boolean isAvailable,
        Long courseId,
        Long lessonTypeId,
        String lessonTypeName
) {
}
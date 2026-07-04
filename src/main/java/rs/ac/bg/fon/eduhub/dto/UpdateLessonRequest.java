package rs.ac.bg.fon.eduhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Zahtev za izmenu postojeće lekcije (SO13).
 *
 * @param lessonTitle novi naslov lekcije
 * @param lessonOrderIndex novi redni broj lekcije u okviru kursa
 * @param lessonTypeId identifikator novog tipa lekcije
 * @param isAvailable da li lekcija treba da bude dostupna studentima
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record UpdateLessonRequest(
        @NotBlank @Size(max = 150) String lessonTitle,
        @NotNull Integer lessonOrderIndex,
        @NotNull Long lessonTypeId,
        @NotNull Boolean isAvailable
) {
}
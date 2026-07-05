package rs.ac.bg.fon.eduhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Zahtev za dodavanje nove lekcije na kurs (SO12).
 *
 * @param lessonTitle naslov lekcije
 * @param lessonOrderIndex redni broj lekcije u okviru kursa
 * @param lessonTypeId identifikator tipa lekcije
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record CreateLessonRequest(
        @NotBlank @Size(max = 150) String lessonTitle,
        @NotNull Integer lessonOrderIndex,
        @NotNull Long lessonTypeId
) {
}
package rs.ac.bg.fon.eduhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Zahtev za kreiranje novog kursa (SO7).
 *
 * @param courseTitle naslov kursa
 * @param courseDescription opis kursa
 * @param courseCategoryId identifikator kategorije kursa
 * @param courseLevelId identifikator nivoa kursa
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record CreateCourseRequest(
        @NotBlank @Size(max = 150) String courseTitle,
        @NotBlank String courseDescription,
        @NotNull Long courseCategoryId,
        @NotNull Long courseLevelId
) {
}
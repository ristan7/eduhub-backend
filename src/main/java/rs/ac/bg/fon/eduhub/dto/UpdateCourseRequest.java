package rs.ac.bg.fon.eduhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Zahtev za izmenu postojećeg kursa (SO8).
 *
 * @param courseTitle novi naslov kursa
 * @param courseDescription novi opis kursa
 * @param courseCategoryId identifikator nove kategorije kursa
 * @param courseLevelId identifikator novog nivoa kursa
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record UpdateCourseRequest(
        @NotBlank @Size(max = 150) String courseTitle,
        @NotBlank String courseDescription,
        @NotNull Long courseCategoryId,
        @NotNull Long courseLevelId
) {
}
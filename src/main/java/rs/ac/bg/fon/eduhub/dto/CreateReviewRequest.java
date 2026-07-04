package rs.ac.bg.fon.eduhub.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Zahtev za ocenjivanje kursa (SO19).
 *
 * @param rating numerička ocena kursa, u opsegu od 1 do 5
 * @param comment tekstualni komentar, opciono
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record CreateReviewRequest(
        @NotNull @Min(1) @Max(5) Integer rating,
        String comment
) {
}
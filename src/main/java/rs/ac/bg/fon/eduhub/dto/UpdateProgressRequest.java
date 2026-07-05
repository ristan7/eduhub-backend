package rs.ac.bg.fon.eduhub.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Zahtev za ažuriranje napretka studenta kroz kurs (SO18).
 *
 * @param progressPercentage novi procenat napretka, u opsegu od 0 do 100
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record UpdateProgressRequest(
        @NotNull @Min(0) @Max(100) Integer progressPercentage
) {
}
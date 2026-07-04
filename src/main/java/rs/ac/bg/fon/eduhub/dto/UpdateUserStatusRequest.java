package rs.ac.bg.fon.eduhub.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Zahtev za promenu statusa aktivnosti naloga korisnika (SO21).
 *
 * @param isActive nova vrednost statusa aktivnosti naloga
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record UpdateUserStatusRequest(
        @NotNull Boolean isActive
) {
}
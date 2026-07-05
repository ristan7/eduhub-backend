package rs.ac.bg.fon.eduhub.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Zahtev za dodelu nove uloge korisniku (SO22).
 *
 * @param roleId identifikator uloge koja se dodeljuje korisniku
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record UpdateUserRoleRequest(
        @NotNull Long roleId
) {
}
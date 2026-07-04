package rs.ac.bg.fon.eduhub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Zahtev za prijavu korisnika na sistem (SO2).
 *
 * @param userEmail email adresa korisnika
 * @param password lozinka korisnika
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record LoginRequest(
        @NotBlank(message = "Email is required.")
        @Email(message = "Email must be valid.")
        String userEmail,

        @NotBlank(message = "Password is required.")
        String password
) {
}
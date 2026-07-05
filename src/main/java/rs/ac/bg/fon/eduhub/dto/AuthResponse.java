package rs.ac.bg.fon.eduhub.dto;

/**
 * Odgovor na uspešnu prijavu korisnika, sadrži JWT token i osnovne
 * podatke o korisniku.
 *
 * @param token JWT token koji se koristi za autentifikaciju narednih zahteva
 * @param user podaci o prijavljenom korisniku
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record AuthResponse(
        String token,
        UserDto user
) {
}
package rs.ac.bg.fon.eduhub.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardizovan oblik odgovora za greške validacije ulaznih podataka
 * (npr. neuspešna {@code @Valid} provera), koji za svako neispravno
 * polje navodi konkretnu poruku greške.
 *
 * @param timestamp trenutak nastanka greške
 * @param status HTTP status kod greške (uvek 400)
 * @param error opis tipa greške
 * @param fieldErrors mapa naziva polja i odgovarajuće poruke o grešci validacije
 * @param path putanja zahteva koji je izazvao grešku
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record ValidationErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        Map<String, String> fieldErrors,
        String path
) {
}
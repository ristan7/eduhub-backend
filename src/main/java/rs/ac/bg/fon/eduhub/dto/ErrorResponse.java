package rs.ac.bg.fon.eduhub.dto;

import java.time.LocalDateTime;

/**
 * Standardizovan oblik odgovora za greške koje vraća
 * {@link rs.ac.bg.fon.eduhub.exception.GlobalExceptionHandler}.
 *
 * @param timestamp trenutak nastanka greške
 * @param status HTTP status kod greške
 * @param error naziv HTTP statusa (npr. "Not Found", "Forbidden")
 * @param message opis greške namenjen korisniku
 * @param path putanja zahteva koji je izazvao grešku
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
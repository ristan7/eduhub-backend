package rs.ac.bg.fon.eduhub.client;

/**
 * Predstavlja rezultat jednog HTTP poziva ka backend API-ju.
 *
 * @param statusCode HTTP status kod odgovora (npr. 200, 404, 401).
 * @param body       Telo odgovora u obliku teksta (obično JSON).
 */
public record ApiResponse(int statusCode, String body) {

    /**
     * Proverava da li je odgovor uspešan (status kod u opsegu 200-299).
     *
     * @return {@code true} ako je poziv uspešan, {@code false} u suprotnom.
     */
    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }
}
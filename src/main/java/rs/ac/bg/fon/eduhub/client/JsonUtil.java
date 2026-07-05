package rs.ac.bg.fon.eduhub.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Pomoćna klasa koja obezbeđuje jedan zajednički, ispravno podešen
 * {@link ObjectMapper} za sve delove konzolnog klijenta, kao i metode
 * za jednostavan ispis grešaka koje vraća backend.
 */
public final class JsonUtil {

    private static final ObjectMapper MAPPER = createMapper();

    private JsonUtil() {
    }

    /**
     * Vraća zajednički {@link ObjectMapper}, podešen da razume
     * {@code LocalDateTime} polja iz DTO klasa.
     *
     * @return deljena instanca {@code ObjectMapper}-a.
     */
    public static ObjectMapper mapper() {
        return MAPPER;
    }

    /**
     * Ispisuje poruku o grešci iz JSON tela odgovora servera.
     *
     * <p>Podržava oba oblika greške koje backend vraća: običnu
     * {@code ErrorResponse} (polje {@code message}) i
     * {@code ValidationErrorResponse} (mapa {@code fieldErrors}).</p>
     *
     * @param responseBody JSON telo odgovora servera koje sadrži grešku.
     */
    public static void printError(String responseBody) {
        try {
            JsonNode root = MAPPER.readTree(responseBody);

            if (root.has("fieldErrors")) {
                System.out.println("Greška u validaciji podataka:");
                root.get("fieldErrors").fields().forEachRemaining(entry ->
                        System.out.println("  - " + entry.getKey() + ": " + entry.getValue().asText()));
            } else if (root.has("message")) {
                System.out.println("Greška: " + root.get("message").asText());
            } else {
                System.out.println("Greška: " + responseBody);
            }
        } catch (Exception e) {
            System.out.println("Greška: " + responseBody);
        }
    }

    /**
     * Kreira i podešava novi {@link ObjectMapper} sa podrškom za
     * {@code java.time} tipove (npr. {@code LocalDateTime}).
     *
     * @return podešen {@code ObjectMapper}.
     */
    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
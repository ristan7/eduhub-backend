package rs.ac.bg.fon.eduhub.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import rs.ac.bg.fon.eduhub.dto.ErrorResponse;
import rs.ac.bg.fon.eduhub.dto.ValidationErrorResponse;

/**
 * Globalni obrađivač izuzetaka koji hvata izuzetke bačene iz servisnog
 * sloja i konvertuje ih u konzistentne JSON odgovore sa odgovarajućim
 * HTTP statusima, umesto podrazumevanih Spring stranica greške.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Obrađuje {@link IllegalArgumentException}, koji servisni sloj baca
     * za neispravan unos ili entitet koji ne postoji. Ako poruka sadrži
     * reč "not found", vraća HTTP 404, u suprotnom HTTP 400.
     *
     * @param ex uhvaćeni izuzetak
     * @param request HTTP zahtev koji je izazvao grešku
     * @return odgovor sa HTTP 400 ili 404 statusom i opisom greške
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        HttpStatus status = ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;
        return buildResponse(status, ex.getMessage(), request);
    }

    /**
     * Obrađuje {@link IllegalStateException}, koji servisni sloj baca
     * kada podrazumevani (šifrarnički) podaci nedostaju u bazi - greška
     * u konfiguraciji servera, ne korisnička greška.
     *
     * @param ex uhvaćeni izuzetak
     * @param request HTTP zahtev koji je izazvao grešku
     * @return odgovor sa HTTP 500 statusom i opisom greške
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    /**
     * Obrađuje {@link AccessDeniedException}, koji se baca kada korisnik
     * nema dovoljna ovlašćenja za traženu operaciju (npr. nije autor
     * kursa niti administrator).
     *
     * @param ex uhvaćeni izuzetak
     * @param request HTTP zahtev koji je izazvao grešku
     * @return odgovor sa HTTP 403 statusom i opisom greške
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    /**
     * Obrađuje {@link BadCredentialsException}, koji Spring Security
     * baca kada email ili lozinka pri prijavi nisu ispravni.
     *
     * @param ex uhvaćeni izuzetak
     * @param request HTTP zahtev koji je izazvao grešku
     * @return odgovor sa HTTP 401 statusom i generičkom porukom (ne otkriva da li je email ili lozinka pogrešna)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password.", request);
    }

    /**
     * Obrađuje ostale izuzetke autentifikacije koje ne pokriva
     * {@link #handleBadCredentials}.
     *
     * @param ex uhvaćeni izuzetak
     * @param request HTTP zahtev koji je izazvao grešku
     * @return odgovor sa HTTP 401 statusom i opisom greške
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    /**
     * Obrađuje neuspešnu {@code @Valid} validaciju DTO polja, vraćajući
     * mapu svih neispravnih polja sa odgovarajućim porukama.
     *
     * @param ex uhvaćeni izuzetak koji sadrži rezultate validacije
     * @param request HTTP zahtev koji je izazvao grešku
     * @return odgovor sa HTTP 400 statusom i mapom grešaka po poljima
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                                    HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                fieldErrors.put(fe.getField(), fe.getDefaultMessage()));

        ValidationErrorResponse body = new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                fieldErrors,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Obrađuje sve ostale, neočekivane izuzetke koje ne pokriva nijedan
     * specifičniji handler u ovoj klasi.
     *
     * @param ex uhvaćeni izuzetak
     * @param request HTTP zahtev koji je izazvao grešku
     * @return odgovor sa HTTP 500 statusom i generičkom porukom
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", request);
    }

    /**
     * Pomoćna metoda koja gradi standardizovan {@link ErrorResponse}
     * objekat za dati status i poruku.
     *
     * @param status HTTP status greške
     * @param message opis greške
     * @param request HTTP zahtev koji je izazvao grešku
     * @return odgovor sa datim statusom i telom greške
     */
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}
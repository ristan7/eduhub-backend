package rs.ac.bg.fon.eduhub.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Klijent zadužen za slanje HTTP zahteva ka EduHub backend REST API-ju.
 *
 * <p>Svi pozivi automatski dobijaju {@code Authorization} header sa JWT
 * tokenom ukoliko je korisnik trenutno ulogovan (videti {@link SessionContext}).</p>
 */
public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080";

    private final HttpClient httpClient;

    /**
     * Kreira novi {@code ApiClient} sa podrazumevanim tajmautom konekcije od 5 sekundi.
     */
    public ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    /**
     * Šalje GET zahtev na dati API put.
     *
     * @param path putanja na koju se šalje zahtev (npr. {@code "/api/courses"}).
     * @return odgovor servera.
     * @throws IOException          ako dođe do greške u komunikaciji.
     * @throws InterruptedException ako je nit prekinuta tokom čekanja na odgovor.
     */
    public ApiResponse get(String path) throws IOException, InterruptedException {
        HttpRequest request = baseRequestBuilder(path)
                .GET()
                .build();
        return send(request);
    }

    /**
     * Šalje POST zahtev sa JSON telom na dati API put.
     *
     * @param path        putanja na koju se šalje zahtev.
     * @param jsonBody    telo zahteva u JSON formatu.
     * @return odgovor servera.
     * @throws IOException          ako dođe do greške u komunikaciji.
     * @throws InterruptedException ako je nit prekinuta tokom čekanja na odgovor.
     */
    public ApiResponse post(String path, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = baseRequestBuilder(path)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return send(request);
    }

    /**
     * Šalje PUT zahtev sa JSON telom na dati API put.
     *
     * @param path     putanja na koju se šalje zahtev.
     * @param jsonBody telo zahteva u JSON formatu.
     * @return odgovor servera.
     * @throws IOException          ako dođe do greške u komunikaciji.
     * @throws InterruptedException ako je nit prekinuta tokom čekanja na odgovor.
     */
    public ApiResponse put(String path, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = baseRequestBuilder(path)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return send(request);
    }

    /**
     * Šalje DELETE zahtev na dati API put.
     *
     * @param path putanja na koju se šalje zahtev.
     * @return odgovor servera.
     * @throws IOException          ako dođe do greške u komunikaciji.
     * @throws InterruptedException ako je nit prekinuta tokom čekanja na odgovor.
     */
    public ApiResponse delete(String path) throws IOException, InterruptedException {
        HttpRequest request = baseRequestBuilder(path)
                .DELETE()
                .build();
        return send(request);
    }

    /**
     * Šalje PATCH zahtev sa JSON telom na dati API put.
     *
     * @param path     putanja na koju se šalje zahtev.
     * @param jsonBody telo zahteva u JSON formatu.
     * @return odgovor servera.
     * @throws IOException          ako dođe do greške u komunikaciji.
     * @throws InterruptedException ako je nit prekinuta tokom čekanja na odgovor.
     */
    public ApiResponse patch(String path, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = baseRequestBuilder(path)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return send(request);
    }

    /**
     * Kreira osnovni {@link HttpRequest.Builder} sa podešenim URI-jem, JSON
     * content-type headerom i, ukoliko postoji, JWT Authorization headerom.
     *
     * @param path putanja na koju se šalje zahtev.
     * @return builder spreman za dodavanje HTTP metode i tela.
     */
    private HttpRequest.Builder baseRequestBuilder(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json");

        String token = SessionContext.getInstance().getToken();
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        return builder;
    }

    /**
     * Izvršava dati HTTP zahtev i pretvara odgovor u {@link ApiResponse}.
     *
     * @param request HTTP zahtev koji treba izvršiti.
     * @return odgovor servera.
     * @throws IOException          ako dođe do greške u komunikaciji.
     * @throws InterruptedException ako je nit prekinuta tokom čekanja na odgovor.
     */
    private ApiResponse send(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return new ApiResponse(response.statusCode(), response.body());
    }
}
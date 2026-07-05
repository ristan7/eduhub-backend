package rs.ac.bg.fon.eduhub.client.menu;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Scanner;
import rs.ac.bg.fon.eduhub.client.ApiClient;
import rs.ac.bg.fon.eduhub.client.ApiResponse;
import rs.ac.bg.fon.eduhub.client.JsonUtil;
import rs.ac.bg.fon.eduhub.client.SessionContext;
import rs.ac.bg.fon.eduhub.dto.CreateReviewRequest;
import rs.ac.bg.fon.eduhub.dto.ReviewDto;

/**
 * Podmeni konzolnog klijenta za ocenjivanje kurseva i pregled ocena.
 */
public class ReviewMenu {

    /**
     * Prikazuje podmeni za recenzije u petlji, dok korisnik ne izabere
     * povratak na glavni meni.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    public static void show(ApiClient apiClient, Scanner scanner) {
        boolean back = false;

        while (!back) {
            System.out.println();
            System.out.println("----- Recenzije -----");
            System.out.println("1. Oceni kurs (prijava obavezna)");
            System.out.println("2. Pregled ocena kursa (prijava obavezna)");
            System.out.println("0. Nazad na glavni meni");
            System.out.print("Izaberi opciju: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> addReview(apiClient, scanner);
                case "2" -> listReviewsByCourse(apiClient, scanner);
                case "0" -> back = true;
                default -> System.out.println("Nepoznata opcija, pokušaj ponovo.");
            }
        }
    }

    /**
     * Dodaje ocenu i komentar za kurs, vezano za konkretnu prijavu.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void addReview(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("ID prijave (enrollment) koju ocenjuješ: ");
        String enrollmentId = scanner.nextLine().trim();
        System.out.print("Ocena (1-5): ");
        String rating = scanner.nextLine().trim();
        System.out.print("Komentar (Enter za preskakanje): ");
        String comment = scanner.nextLine().trim();

        try {
            CreateReviewRequest request = new CreateReviewRequest(
                    Integer.valueOf(rating), comment.isBlank() ? null : comment);
            String jsonBody = JsonUtil.mapper().writeValueAsString(request);

            ApiResponse response = apiClient.post("/api/enrollments/" + enrollmentId + "/review", jsonBody);

            if (response.isSuccessful()) {
                ReviewDto review = JsonUtil.mapper().readValue(response.body(), ReviewDto.class);
                System.out.println("Ocena uspešno dodata!");
                printReviewDetails(review);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (NumberFormatException e) {
            System.out.println("Ocena mora biti broj od 1 do 5.");
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Preuzima i ispisuje sve ocene ostavljene za zadati kurs.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void listReviewsByCourse(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("ID kursa: ");
        String courseId = scanner.nextLine().trim();

        try {
            ApiResponse response = apiClient.get("/api/courses/" + courseId + "/reviews");

            if (response.isSuccessful()) {
                List<ReviewDto> reviews = JsonUtil.mapper().readValue(response.body(), new TypeReference<List<ReviewDto>>() {
                });
                printReviewList(reviews);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Proverava da li je korisnik ulogovan i, ukoliko nije, ispisuje
     * odgovarajuće upozorenje.
     *
     * @return {@code true} ako je korisnik ulogovan, {@code false} u suprotnom.
     */
    private static boolean requireLogin() {
        if (!SessionContext.getInstance().isLoggedIn()) {
            System.out.println("Ova opcija zahteva prijavu. Uloguj se prvo preko menija za autentifikaciju.");
            return false;
        }
        return true;
    }

    /**
     * Ispisuje kratak pregled liste ocena (ocena, autor, komentar).
     *
     * @param reviews lista ocena za ispis.
     */
    private static void printReviewList(List<ReviewDto> reviews) {
        if (reviews.isEmpty()) {
            System.out.println("Nema ocena za prikaz.");
            return;
        }

        System.out.println("Pronađeno ocena: " + reviews.size());
        for (ReviewDto review : reviews) {
            System.out.println("  [" + review.reviewId() + "] " + review.rating() + "/5 - "
                    + review.studentFullName() + (review.comment() != null ? ": " + review.comment() : ""));
        }
    }

    /**
     * Ispisuje pun detalj jedne ocene.
     *
     * @param review ocena čiji se detalji ispisuju.
     */
    private static void printReviewDetails(ReviewDto review) {
        System.out.println("ID: " + review.reviewId());
        System.out.println("Kurs: " + review.courseTitle());
        System.out.println("Ocena: " + review.rating() + "/5");
        System.out.println("Komentar: " + (review.comment() != null ? review.comment() : "-"));
        System.out.println("Autor: " + review.studentFullName());
        System.out.println("Kreirana: " + review.createdAt());
    }
}
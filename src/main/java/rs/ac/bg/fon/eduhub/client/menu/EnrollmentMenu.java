package rs.ac.bg.fon.eduhub.client.menu;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Scanner;
import rs.ac.bg.fon.eduhub.client.ApiClient;
import rs.ac.bg.fon.eduhub.client.ApiResponse;
import rs.ac.bg.fon.eduhub.client.JsonUtil;
import rs.ac.bg.fon.eduhub.client.SessionContext;
import rs.ac.bg.fon.eduhub.dto.CreateEnrollmentRequest;
import rs.ac.bg.fon.eduhub.dto.EnrollmentDto;
import rs.ac.bg.fon.eduhub.dto.UpdateProgressRequest;

/**
 * Podmeni konzolnog klijenta za prijavu studenta na kurs, pregled
 * sopstvenih prijava i ažuriranje napretka. Sve operacije zahtevaju
 * prijavu na sistem.
 */
public class EnrollmentMenu {

    /**
     * Prikazuje podmeni za prijave u petlji, dok korisnik ne izabere
     * povratak na glavni meni.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    public static void show(ApiClient apiClient, Scanner scanner) {
        boolean back = false;

        while (!back) {
            System.out.println();
            System.out.println("----- Upis na kurs (Enrollment) -----");
            System.out.println("1. Prijava na kurs");
            System.out.println("2. Moje prijave");
            System.out.println("3. Ažuriranje napretka");
            System.out.println("0. Nazad na glavni meni");
            System.out.print("Izaberi opciju: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> enroll(apiClient, scanner);
                case "2" -> listMyEnrollments(apiClient);
                case "3" -> updateProgress(apiClient, scanner);
                case "0" -> back = true;
                default -> System.out.println("Nepoznata opcija, pokušaj ponovo.");
            }
        }
    }

    /**
     * Prijavljuje trenutno ulogovanog korisnika na zadati kurs.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void enroll(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("ID kursa na koji se prijavljuješ: ");
        String courseId = scanner.nextLine().trim();

        try {
            CreateEnrollmentRequest request = new CreateEnrollmentRequest(Long.valueOf(courseId));
            String jsonBody = JsonUtil.mapper().writeValueAsString(request);

            ApiResponse response = apiClient.post("/api/enrollments", jsonBody);

            if (response.isSuccessful()) {
                EnrollmentDto enrollment = JsonUtil.mapper().readValue(response.body(), EnrollmentDto.class);
                System.out.println("Uspešno prijavljen/a na kurs!");
                printEnrollmentDetails(enrollment);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (NumberFormatException e) {
            System.out.println("ID kursa mora biti broj.");
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Preuzima i ispisuje sve prijave trenutno ulogovanog korisnika.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     */
    private static void listMyEnrollments(ApiClient apiClient) {
        if (!requireLogin()) {
            return;
        }

        try {
            ApiResponse response = apiClient.get("/api/enrollments/me");

            if (response.isSuccessful()) {
                List<EnrollmentDto> enrollments = JsonUtil.mapper().readValue(response.body(), new TypeReference<List<EnrollmentDto>>() {
                });
                printEnrollmentList(enrollments);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Ažurira procenat napretka za zadatu prijavu.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void updateProgress(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("ID prijave: ");
        String enrollmentId = scanner.nextLine().trim();
        System.out.print("Novi procenat napretka (0-100): ");
        String progress = scanner.nextLine().trim();

        try {
            UpdateProgressRequest request = new UpdateProgressRequest(Integer.valueOf(progress));
            String jsonBody = JsonUtil.mapper().writeValueAsString(request);

            ApiResponse response = apiClient.patch("/api/enrollments/" + enrollmentId + "/progress", jsonBody);

            if (response.isSuccessful()) {
                EnrollmentDto enrollment = JsonUtil.mapper().readValue(response.body(), EnrollmentDto.class);
                System.out.println("Napredak uspešno ažuriran!");
                printEnrollmentDetails(enrollment);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (NumberFormatException e) {
            System.out.println("ID prijave i procenat napretka moraju biti brojevi.");
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
     * Ispisuje kratak pregled liste prijava (ID, kurs, napredak, status).
     *
     * @param enrollments lista prijava za ispis.
     */
    private static void printEnrollmentList(List<EnrollmentDto> enrollments) {
        if (enrollments.isEmpty()) {
            System.out.println("Nemaš nijednu prijavu.");
            return;
        }

        System.out.println("Pronađeno prijava: " + enrollments.size());
        for (EnrollmentDto enrollment : enrollments) {
            System.out.println("  [" + enrollment.enrollmentId() + "] " + enrollment.courseTitle()
                    + " - napredak: " + enrollment.progressPercentage() + "%"
                    + ", status: " + enrollment.enrollmentStatusName());
        }
    }

    /**
     * Ispisuje pun detalj jedne prijave.
     *
     * @param enrollment prijava čiji se detalji ispisuju.
     */
    private static void printEnrollmentDetails(EnrollmentDto enrollment) {
        System.out.println("ID prijave: " + enrollment.enrollmentId());
        System.out.println("Kurs: " + enrollment.courseTitle());
        System.out.println("Student: " + enrollment.studentFullName());
        System.out.println("Napredak: " + enrollment.progressPercentage() + "%");
        System.out.println("Status: " + enrollment.enrollmentStatusName());
        System.out.println("Prijavljen: " + enrollment.enrolledAt());
        System.out.println("Završen: " + (enrollment.completedAt() != null ? enrollment.completedAt() : "-"));
    }
}
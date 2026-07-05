package rs.ac.bg.fon.eduhub.client.menu;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Scanner;
import rs.ac.bg.fon.eduhub.client.ApiClient;
import rs.ac.bg.fon.eduhub.client.ApiResponse;
import rs.ac.bg.fon.eduhub.client.JsonUtil;
import rs.ac.bg.fon.eduhub.client.SessionContext;
import rs.ac.bg.fon.eduhub.dto.CreateLessonRequest;
import rs.ac.bg.fon.eduhub.dto.LessonDto;
import rs.ac.bg.fon.eduhub.dto.UpdateLessonRequest;

/**
 * Podmeni konzolnog klijenta za pregled i upravljanje lekcijama u
 * okviru kursa.
 *
 * <p>Napomena: identifikator tipa lekcije ({@code lessonTypeId}) se unosi
 * ručno; vrednosti se mogu proveriti u tabeli {@code lesson_type}
 * (1=VIDEO, 2=ARTICLE, 3=QUIZ, 4=ASSIGNMENT prema seed podacima).</p>
 */
public class LessonMenu {

    /**
     * Prikazuje podmeni za lekcije u petlji, dok korisnik ne izabere
     * povratak na glavni meni.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    public static void show(ApiClient apiClient, Scanner scanner) {
        boolean back = false;

        while (!back) {
            System.out.println();
            System.out.println("----- Lekcije -----");
            System.out.println("1. Prikaz lekcija kursa (prijava obavezna)");
            System.out.println("2. Dodavanje lekcije (prijava obavezna)");
            System.out.println("3. Izmena lekcije (prijava obavezna)");
            System.out.println("4. Brisanje lekcije (prijava obavezna)");
            System.out.println("0. Nazad na glavni meni");
            System.out.print("Izaberi opciju: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> listLessonsByCourse(apiClient, scanner);
                case "2" -> addLesson(apiClient, scanner);
                case "3" -> updateLesson(apiClient, scanner);
                case "4" -> deleteLesson(apiClient, scanner);
                case "0" -> back = true;
                default -> System.out.println("Nepoznata opcija, pokušaj ponovo.");
            }
        }
    }

    /**
     * Preuzima i ispisuje sve lekcije zadatog kursa.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void listLessonsByCourse(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("ID kursa: ");
        String courseId = scanner.nextLine().trim();

        try {
            ApiResponse response = apiClient.get("/api/courses/" + courseId + "/lessons");

            if (response.isSuccessful()) {
                List<LessonDto> lessons = JsonUtil.mapper().readValue(response.body(), new TypeReference<List<LessonDto>>() {
                });
                printLessonList(lessons);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Dodaje novu lekciju na zadati kurs.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void addLesson(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("ID kursa na koji se dodaje lekcija: ");
        String courseId = scanner.nextLine().trim();
        System.out.print("Naslov lekcije: ");
        String title = scanner.nextLine().trim();
        System.out.print("Redni broj lekcije (npr. 1, 2, 3...): ");
        String orderIndex = scanner.nextLine().trim();
        System.out.print("ID tipa lekcije (1=VIDEO, 2=ARTICLE, 3=QUIZ, 4=ASSIGNMENT): ");
        String lessonTypeId = scanner.nextLine().trim();

        try {
            CreateLessonRequest request = new CreateLessonRequest(
                    title, Integer.valueOf(orderIndex), Long.valueOf(lessonTypeId));
            String jsonBody = JsonUtil.mapper().writeValueAsString(request);

            ApiResponse response = apiClient.post("/api/courses/" + courseId + "/lessons", jsonBody);

            if (response.isSuccessful()) {
                LessonDto lesson = JsonUtil.mapper().readValue(response.body(), LessonDto.class);
                System.out.println("Lekcija uspešno dodata!");
                printLessonDetails(lesson);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (NumberFormatException e) {
            System.out.println("Redni broj i ID tipa lekcije moraju biti brojevi.");
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Izmenjuje podatke postojeće lekcije.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void updateLesson(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("ID lekcije koja se menja: ");
        String id = scanner.nextLine().trim();
        System.out.print("Novi naslov lekcije: ");
        String title = scanner.nextLine().trim();
        System.out.print("Novi redni broj lekcije: ");
        String orderIndex = scanner.nextLine().trim();
        System.out.print("Novi ID tipa lekcije (1=VIDEO, 2=ARTICLE, 3=QUIZ, 4=ASSIGNMENT): ");
        String lessonTypeId = scanner.nextLine().trim();
        System.out.print("Da li je lekcija dostupna studentima? (da/ne): ");
        boolean isAvailable = scanner.nextLine().trim().equalsIgnoreCase("da");

        try {
            UpdateLessonRequest request = new UpdateLessonRequest(
                    title, Integer.valueOf(orderIndex), Long.valueOf(lessonTypeId), isAvailable);
            String jsonBody = JsonUtil.mapper().writeValueAsString(request);

            ApiResponse response = apiClient.put("/api/lessons/" + id, jsonBody);

            if (response.isSuccessful()) {
                LessonDto lesson = JsonUtil.mapper().readValue(response.body(), LessonDto.class);
                System.out.println("Lekcija uspešno izmenjena!");
                printLessonDetails(lesson);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (NumberFormatException e) {
            System.out.println("Redni broj i ID tipa lekcije moraju biti brojevi.");
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Trajno briše lekciju na osnovu unetog ID-ja.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void deleteLesson(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("ID lekcije koja se briše: ");
        String id = scanner.nextLine().trim();

        try {
            ApiResponse response = apiClient.delete("/api/lessons/" + id);

            if (response.isSuccessful()) {
                System.out.println("Lekcija uspešno obrisana.");
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
     * Ispisuje kratak pregled liste lekcija (ID, redni broj i naslov).
     *
     * @param lessons lista lekcija za ispis.
     */
    private static void printLessonList(List<LessonDto> lessons) {
        if (lessons.isEmpty()) {
            System.out.println("Nema lekcija za prikaz.");
            return;
        }

        System.out.println("Pronađeno lekcija: " + lessons.size());
        for (LessonDto lesson : lessons) {
            System.out.println("  [" + lesson.lessonId() + "] #" + lesson.lessonOrderIndex() + " "
                    + lesson.lessonTitle() + " (tip: " + lesson.lessonTypeName()
                    + ", dostupna: " + lesson.isAvailable() + ")");
        }
    }

    /**
     * Ispisuje pun detalj jedne lekcije.
     *
     * @param lesson lekcija čiji se detalji ispisuju.
     */
    private static void printLessonDetails(LessonDto lesson) {
        System.out.println("ID: " + lesson.lessonId());
        System.out.println("Naslov: " + lesson.lessonTitle());
        System.out.println("Redni broj: " + lesson.lessonOrderIndex());
        System.out.println("Tip: " + lesson.lessonTypeName());
        System.out.println("Dostupna studentima: " + lesson.isAvailable());
        System.out.println("ID kursa: " + lesson.courseId());
        System.out.println("Kreirana: " + lesson.createdAt());
    }
}
package rs.ac.bg.fon.eduhub.client.menu;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Scanner;
import rs.ac.bg.fon.eduhub.client.ApiClient;
import rs.ac.bg.fon.eduhub.client.ApiResponse;
import rs.ac.bg.fon.eduhub.client.JsonUtil;
import rs.ac.bg.fon.eduhub.client.SessionContext;
import rs.ac.bg.fon.eduhub.dto.CourseDto;
import rs.ac.bg.fon.eduhub.dto.CreateCourseRequest;
import rs.ac.bg.fon.eduhub.dto.UpdateCourseRequest;

/**
 * Podmeni konzolnog klijenta za pregled, pretragu i upravljanje kursevima.
 *
 * <p>Napomena: identifikatori kategorije i nivoa kursa (courseCategoryId,
 * courseLevelId) se unose ručno, jer backend trenutno ne izlaže endpoint
 * za listanje tih šifarnika. Njihove vrednosti se mogu proveriti direktno
 * u bazi (tabele {@code course_category} i {@code course_level}).</p>
 */
public class CourseMenu {

    /**
     * Prikazuje podmeni za kurseve u petlji, dok korisnik ne izabere
     * povratak na glavni meni.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    public static void show(ApiClient apiClient, Scanner scanner) {
        boolean back = false;

        while (!back) {
            System.out.println();
            System.out.println("----- Kursevi -----");
            System.out.println("1. Prikaz svih kurseva");
            System.out.println("2. Pretraga kurseva");
            System.out.println("3. Detalji kursa po ID-ju");
            System.out.println("4. Kreiranje novog kursa (prijava obavezna)");
            System.out.println("5. Izmena kursa (prijava obavezna)");
            System.out.println("6. Deaktivacija kursa (prijava obavezna)");
            System.out.println("0. Nazad na glavni meni");
            System.out.print("Izaberi opciju: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> listAllCourses(apiClient);
                case "2" -> searchCourses(apiClient, scanner);
                case "3" -> getCourseById(apiClient, scanner);
                case "4" -> createCourse(apiClient, scanner);
                case "5" -> updateCourse(apiClient, scanner);
                case "6" -> deactivateCourse(apiClient, scanner);
                case "0" -> back = true;
                default -> System.out.println("Nepoznata opcija, pokušaj ponovo.");
            }
        }
    }

    /**
     * Preuzima i ispisuje sve kurseve.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     */
    private static void listAllCourses(ApiClient apiClient) {
        try {
            ApiResponse response = apiClient.get("/api/courses");

            if (response.isSuccessful()) {
                List<CourseDto> courses = JsonUtil.mapper().readValue(response.body(), new TypeReference<List<CourseDto>>() {
                });
                printCourseList(courses);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Preuzima i ispisuje kurseve prema unetim filterima (ključna reč,
     * kategorija, nivo). Svaki filter je opcion — prazan unos ga preskače.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void searchCourses(ApiClient apiClient, Scanner scanner) {
        System.out.print("Ključna reč (Enter za preskakanje): ");
        String keyword = scanner.nextLine().trim();
        System.out.print("ID kategorije (Enter za preskakanje): ");
        String categoryId = scanner.nextLine().trim();
        System.out.print("ID nivoa (Enter za preskakanje): ");
        String levelId = scanner.nextLine().trim();

        StringBuilder path = new StringBuilder("/api/courses?");
        if (!keyword.isBlank()) {
            path.append("keyword=").append(keyword).append("&");
        }
        if (!categoryId.isBlank()) {
            path.append("categoryId=").append(categoryId).append("&");
        }
        if (!levelId.isBlank()) {
            path.append("levelId=").append(levelId).append("&");
        }

        try {
            ApiResponse response = apiClient.get(path.toString());

            if (response.isSuccessful()) {
                List<CourseDto> courses = JsonUtil.mapper().readValue(response.body(), new TypeReference<List<CourseDto>>() {
                });
                printCourseList(courses);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Preuzima i ispisuje detalje jednog kursa na osnovu unetog ID-ja.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void getCourseById(ApiClient apiClient, Scanner scanner) {
        System.out.print("ID kursa: ");
        String id = scanner.nextLine().trim();

        try {
            ApiResponse response = apiClient.get("/api/courses/" + id);

            if (response.isSuccessful()) {
                CourseDto course = JsonUtil.mapper().readValue(response.body(), CourseDto.class);
                printCourseDetails(course);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Kreira novi kurs sa trenutno prijavljenim korisnikom kao autorom.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void createCourse(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("Naslov kursa: ");
        String title = scanner.nextLine().trim();
        System.out.print("Opis kursa: ");
        String description = scanner.nextLine().trim();
        System.out.print("ID kategorije: ");
        Long categoryId = Long.valueOf(scanner.nextLine().trim());
        System.out.print("ID nivoa: ");
        Long levelId = Long.valueOf(scanner.nextLine().trim());

        try {
            CreateCourseRequest request = new CreateCourseRequest(title, description, categoryId, levelId);
            String jsonBody = JsonUtil.mapper().writeValueAsString(request);

            ApiResponse response = apiClient.post("/api/courses", jsonBody);

            if (response.isSuccessful()) {
                CourseDto course = JsonUtil.mapper().readValue(response.body(), CourseDto.class);
                System.out.println("Kurs uspešno kreiran!");
                printCourseDetails(course);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (NumberFormatException e) {
            System.out.println("ID kategorije i nivoa moraju biti brojevi.");
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Izmenjuje podatke postojećeg kursa.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void updateCourse(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("ID kursa koji se menja: ");
        String id = scanner.nextLine().trim();
        System.out.print("Novi naslov kursa: ");
        String title = scanner.nextLine().trim();
        System.out.print("Novi opis kursa: ");
        String description = scanner.nextLine().trim();
        System.out.print("Novi ID kategorije: ");
        Long categoryId = Long.valueOf(scanner.nextLine().trim());
        System.out.print("Novi ID nivoa: ");
        Long levelId = Long.valueOf(scanner.nextLine().trim());

        try {
            UpdateCourseRequest request = new UpdateCourseRequest(title, description, categoryId, levelId);
            String jsonBody = JsonUtil.mapper().writeValueAsString(request);

            ApiResponse response = apiClient.put("/api/courses/" + id, jsonBody);

            if (response.isSuccessful()) {
                CourseDto course = JsonUtil.mapper().readValue(response.body(), CourseDto.class);
                System.out.println("Kurs uspešno izmenjen!");
                printCourseDetails(course);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (NumberFormatException e) {
            System.out.println("ID kategorije i nivoa moraju biti brojevi.");
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Deaktivira (arhivira) kurs na osnovu unetog ID-ja.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void deactivateCourse(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("ID kursa koji se deaktivira: ");
        String id = scanner.nextLine().trim();

        try {
            ApiResponse response = apiClient.delete("/api/courses/" + id);

            if (response.isSuccessful()) {
                System.out.println("Kurs uspešno deaktiviran.");
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
     * Ispisuje kratak pregled liste kurseva (ID i naslov).
     *
     * @param courses lista kurseva za ispis.
     */
    private static void printCourseList(List<CourseDto> courses) {
        if (courses.isEmpty()) {
            System.out.println("Nema kurseva za prikaz.");
            return;
        }

        System.out.println("Pronađeno kurseva: " + courses.size());
        for (CourseDto course : courses) {
            System.out.println("  [" + course.courseId() + "] " + course.courseTitle()
                    + " (autor: " + course.authorFullName() + ", kategorija: " + course.courseCategoryName()
                    + ", nivo: " + course.courseLevelName() + ")");
        }
    }

    /**
     * Ispisuje pun detalj jednog kursa.
     *
     * @param course kurs čiji se detalji ispisuju.
     */
    private static void printCourseDetails(CourseDto course) {
        System.out.println("ID: " + course.courseId());
        System.out.println("Naslov: " + course.courseTitle());
        System.out.println("Opis: " + course.courseDescription());
        System.out.println("Autor: " + course.authorFullName());
        System.out.println("Kategorija: " + course.courseCategoryName());
        System.out.println("Nivo: " + course.courseLevelName());
        System.out.println("Status: " + course.courseStatusName());
        System.out.println("Objavljen: " + course.isPublished());
        System.out.println("Kreiran: " + course.createdAt());
        System.out.println("Izmenjen: " + course.updatedAt());
    }
}
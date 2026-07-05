package rs.ac.bg.fon.eduhub.client.menu;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Scanner;
import rs.ac.bg.fon.eduhub.client.ApiClient;
import rs.ac.bg.fon.eduhub.client.ApiResponse;
import rs.ac.bg.fon.eduhub.client.JsonUtil;
import rs.ac.bg.fon.eduhub.client.SessionContext;
import rs.ac.bg.fon.eduhub.dto.CourseDto;
import rs.ac.bg.fon.eduhub.dto.PlatformStatisticsDto;
import rs.ac.bg.fon.eduhub.dto.UpdateUserRoleRequest;
import rs.ac.bg.fon.eduhub.dto.UpdateUserStatusRequest;
import rs.ac.bg.fon.eduhub.dto.UserDto;

/**
 * Podmeni konzolnog klijenta za administratorske operacije: upravljanje
 * korisnicima, dodelu uloga, odobravanje/blokiranje kurseva i pregled
 * statistike platforme.
 *
 * <p>Napomena: sve operacije u ovom meniju zahtevaju ulogu ADMIN. Uloga se
 * može proveriti u tabeli {@code role} (1=STUDENT, 2=INSTRUCTOR, 3=ADMIN
 * prema seed podacima).</p>
 */
public class AdminMenu {

    /**
     * Prikazuje admin podmeni u petlji, dok korisnik ne izabere povratak
     * na glavni meni.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    public static void show(ApiClient apiClient, Scanner scanner) {
        boolean back = false;

        while (!back) {
            System.out.println();
            System.out.println("----- Admin -----");
            System.out.println("1. Svi korisnici");
            System.out.println("2. Detalji korisnika po ID-ju");
            System.out.println("3. Promena statusa naloga (aktivan/blokiran)");
            System.out.println("4. Promena uloge korisnika");
            System.out.println("5. Odobri kurs (PUBLISHED)");
            System.out.println("6. Blokiraj kurs (ARCHIVED)");
            System.out.println("7. Statistika platforme");
            System.out.println("0. Nazad na glavni meni");
            System.out.print("Izaberi opciju: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> listAllUsers(apiClient);
                case "2" -> getUserById(apiClient, scanner);
                case "3" -> setUserStatus(apiClient, scanner);
                case "4" -> updateUserRole(apiClient, scanner);
                case "5" -> approveCourse(apiClient, scanner);
                case "6" -> blockCourse(apiClient, scanner);
                case "7" -> showStatistics(apiClient);
                case "0" -> back = true;
                default -> System.out.println("Nepoznata opcija, pokušaj ponovo.");
            }
        }
    }

    /**
     * Preuzima i ispisuje sve registrovane korisnike.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     */
    private static void listAllUsers(ApiClient apiClient) {
        if (!requireLogin()) {
            return;
        }

        try {
            ApiResponse response = apiClient.get("/api/admin/users");

            if (response.isSuccessful()) {
                List<UserDto> users = JsonUtil.mapper().readValue(response.body(), new TypeReference<List<UserDto>>() {
                });
                printUserList(users);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Preuzima i ispisuje detalje jednog korisnika po ID-ju.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void getUserById(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("ID korisnika: ");
        String id = scanner.nextLine().trim();

        try {
            ApiResponse response = apiClient.get("/api/admin/users/" + id);

            if (response.isSuccessful()) {
                UserDto user = JsonUtil.mapper().readValue(response.body(), UserDto.class);
                printUserDetails(user);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Menja status aktivnosti naloga korisnika (aktivan/blokiran).
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void setUserStatus(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("ID korisnika: ");
        String id = scanner.nextLine().trim();
        System.out.print("Da li nalog treba da bude aktivan? (da/ne): ");
        boolean isActive = scanner.nextLine().trim().equalsIgnoreCase("da");

        try {
            UpdateUserStatusRequest request = new UpdateUserStatusRequest(isActive);
            String jsonBody = JsonUtil.mapper().writeValueAsString(request);

            ApiResponse response = apiClient.patch("/api/admin/users/" + id + "/status", jsonBody);

            if (response.isSuccessful()) {
                UserDto user = JsonUtil.mapper().readValue(response.body(), UserDto.class);
                System.out.println("Status naloga uspešno ažuriran!");
                printUserDetails(user);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Dodeljuje novu ulogu korisniku.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void updateUserRole(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("ID korisnika: ");
        String id = scanner.nextLine().trim();
        System.out.print("ID nove uloge (1=STUDENT, 2=INSTRUCTOR, 3=ADMIN): ");
        String roleId = scanner.nextLine().trim();

        try {
            UpdateUserRoleRequest request = new UpdateUserRoleRequest(Long.valueOf(roleId));
            String jsonBody = JsonUtil.mapper().writeValueAsString(request);

            ApiResponse response = apiClient.patch("/api/admin/users/" + id + "/role", jsonBody);

            if (response.isSuccessful()) {
                UserDto user = JsonUtil.mapper().readValue(response.body(), UserDto.class);
                System.out.println("Uloga korisnika uspešno ažurirana!");
                printUserDetails(user);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (NumberFormatException e) {
            System.out.println("ID uloge mora biti broj.");
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Odobrava kurs, postavljajući njegov status na PUBLISHED.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void approveCourse(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("ID kursa koji se odobrava: ");
        String id = scanner.nextLine().trim();

        try {
            ApiResponse response = apiClient.patch("/api/admin/courses/" + id + "/approve", "");

            if (response.isSuccessful()) {
                CourseDto course = JsonUtil.mapper().readValue(response.body(), CourseDto.class);
                System.out.println("Kurs uspešno odobren! Novi status: " + course.courseStatusName());
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Blokira kurs, postavljajući njegov status na ARCHIVED.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void blockCourse(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("ID kursa koji se blokira: ");
        String id = scanner.nextLine().trim();

        try {
            ApiResponse response = apiClient.patch("/api/admin/courses/" + id + "/block", "");

            if (response.isSuccessful()) {
                CourseDto course = JsonUtil.mapper().readValue(response.body(), CourseDto.class);
                System.out.println("Kurs uspešno blokiran! Novi status: " + course.courseStatusName());
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Preuzima i ispisuje zbirnu statistiku platforme.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     */
    private static void showStatistics(ApiClient apiClient) {
        if (!requireLogin()) {
            return;
        }

        try {
            ApiResponse response = apiClient.get("/api/admin/statistics");

            if (response.isSuccessful()) {
                PlatformStatisticsDto stats = JsonUtil.mapper().readValue(response.body(), PlatformStatisticsDto.class);
                printStatistics(stats);
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
            System.out.println("Ova opcija zahteva prijavu sa ulogom ADMIN.");
            return false;
        }
        return true;
    }

    /**
     * Ispisuje kratak pregled liste korisnika (ID, ime, email, uloga, status).
     *
     * @param users lista korisnika za ispis.
     */
    private static void printUserList(List<UserDto> users) {
        if (users.isEmpty()) {
            System.out.println("Nema registrovanih korisnika.");
            return;
        }

        System.out.println("Pronađeno korisnika: " + users.size());
        for (UserDto user : users) {
            System.out.println("  [" + user.userId() + "] " + user.firstName() + " " + user.lastName()
                    + " (" + user.userEmail() + ") - uloga: " + user.roleName()
                    + ", aktivan: " + user.isActive());
        }
    }

    /**
     * Ispisuje pun detalj jednog korisnika.
     *
     * @param user korisnik čiji se detalji ispisuju.
     */
    private static void printUserDetails(UserDto user) {
        System.out.println("ID: " + user.userId());
        System.out.println("Ime i prezime: " + user.firstName() + " " + user.lastName());
        System.out.println("Email: " + user.userEmail());
        System.out.println("Uloga: " + user.roleName());
        System.out.println("Aktivan: " + user.isActive());
        System.out.println("Registrovan: " + user.createdAt());
    }

    /**
     * Ispisuje zbirnu statistiku platforme.
     *
     * @param stats statistika koja se ispisuje.
     */
    private static void printStatistics(PlatformStatisticsDto stats) {
        System.out.println("----- Statistika platforme -----");
        System.out.println("Ukupno korisnika: " + stats.totalUsers());
        System.out.println("  Studenti: " + stats.totalStudents());
        System.out.println("  Predavači: " + stats.totalInstructors());
        System.out.println("  Administratori: " + stats.totalAdmins());
        System.out.println("Ukupno kurseva: " + stats.totalCourses());
        System.out.println("  Objavljeni: " + stats.totalPublishedCourses());
        System.out.println("  U izradi (draft): " + stats.totalDraftCourses());
        System.out.println("  Arhivirani: " + stats.totalArchivedCourses());
        System.out.println("Ukupno prijava na kurseve: " + stats.totalEnrollments());
        System.out.println("Ukupno izdatih sertifikata: " + stats.totalCertificatesIssued());
    }
}
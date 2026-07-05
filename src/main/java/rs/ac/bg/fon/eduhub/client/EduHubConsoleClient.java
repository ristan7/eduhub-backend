package rs.ac.bg.fon.eduhub.client;

import java.util.Scanner;
import rs.ac.bg.fon.eduhub.client.menu.AuthMenu;
import rs.ac.bg.fon.eduhub.client.menu.CourseMenu;
import rs.ac.bg.fon.eduhub.client.menu.LessonMenu;
import rs.ac.bg.fon.eduhub.client.menu.MaterialMenu;
import rs.ac.bg.fon.eduhub.client.menu.EnrollmentMenu;
import rs.ac.bg.fon.eduhub.client.menu.ReviewMenu;
import rs.ac.bg.fon.eduhub.client.menu.CertificateMenu;
import rs.ac.bg.fon.eduhub.client.menu.NotificationMenu;
import rs.ac.bg.fon.eduhub.client.menu.AdminMenu;

/**
 * Ulazna tačka konzolnog klijenta za EduHub backend.
 *
 * <p>Prikazuje glavni meni preko kojeg korisnik bira koju grupu operacija
 * želi da izvrši (autentifikacija, kursevi, lekcije, itd.). Klijent se
 * pokreće nezavisno od Spring Boot aplikacije i komunicira sa njom isključivo
 * preko HTTP poziva ({@link ApiClient}).</p>
 */
public class EduHubConsoleClient {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ApiClient apiClient = new ApiClient();

    /**
     * Pokreće konzolni klijent i prikazuje glavni meni u petlji dok korisnik
     * ne izabere opciju za izlazak.
     *
     * @param args argumenti komandne linije (ne koriste se).
     */
    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> AuthMenu.show(apiClient, scanner);
                case "2" -> CourseMenu.show(apiClient, scanner);
                case "3" -> LessonMenu.show(apiClient, scanner);
                case "4" -> MaterialMenu.show(apiClient, scanner);
                case "5" -> EnrollmentMenu.show(apiClient, scanner);
                case "6" -> ReviewMenu.show(apiClient, scanner);
                case "7" -> CertificateMenu.show(apiClient, scanner);
                case "8" -> NotificationMenu.show(apiClient, scanner);
                case "9" -> AdminMenu.show(apiClient, scanner);
                case "0" -> {
                    running = false;
                    System.out.println("Klijent je zatvoren.");
                }
                default -> System.out.println("Nepoznata opcija, pokušaj ponovo.");
            }
        }

        scanner.close();
    }

    /**
     * Ispisuje glavni meni sa svim dostupnim opcijama.
     */
    private static void printMainMenu() {
        System.out.println();
        System.out.println("========== EduHub Konzolni Klijent ==========");
        if (SessionContext.getInstance().isLoggedIn()) {
            System.out.println("Ulogovan kao: " + SessionContext.getInstance().getUserEmail());
        } else {
            System.out.println("Niste ulogovani.");
        }
        System.out.println("1. Autentifikacija (login/register)");
        System.out.println("2. Kursevi");
        System.out.println("3. Lekcije");
        System.out.println("4. Materijali");
        System.out.println("5. Upis na kurs (enrollment)");
        System.out.println("6. Recenzije");
        System.out.println("7. Sertifikati");
        System.out.println("8. Notifikacije");
        System.out.println("9. Admin");
        System.out.println("0. Izlaz");
        System.out.print("Izaberi opciju: ");
    }
}
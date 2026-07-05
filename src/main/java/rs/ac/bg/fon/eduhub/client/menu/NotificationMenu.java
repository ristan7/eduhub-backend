package rs.ac.bg.fon.eduhub.client.menu;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Scanner;
import rs.ac.bg.fon.eduhub.client.ApiClient;
import rs.ac.bg.fon.eduhub.client.ApiResponse;
import rs.ac.bg.fon.eduhub.client.JsonUtil;
import rs.ac.bg.fon.eduhub.client.SessionContext;
import rs.ac.bg.fon.eduhub.dto.CreateNotificationRequest;
import rs.ac.bg.fon.eduhub.dto.NotificationDto;

/**
 * Podmeni konzolnog klijenta za slanje notifikacija, pregled sopstvenih
 * notifikacija i označavanje notifikacija kao pročitanih.
 *
 * <p>Napomena: identifikator tipa notifikacije ({@code notificationTypeId})
 * se unosi ručno; vrednosti se mogu proveriti u tabeli
 * {@code notification_type} (1=SYSTEM, 2=ENROLLMENT, 3=COURSE,
 * 4=CERTIFICATE, 5=REVIEW prema seed podacima). Slanje notifikacije
 * zahteva ulogu INSTRUCTOR ili ADMIN.</p>
 */
public class NotificationMenu {

    /**
     * Prikazuje podmeni za notifikacije u petlji, dok korisnik ne
     * izabere povratak na glavni meni.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    public static void show(ApiClient apiClient, Scanner scanner) {
        boolean back = false;

        while (!back) {
            System.out.println();
            System.out.println("----- Notifikacije -----");
            System.out.println("1. Pošalji notifikaciju (predavač/admin)");
            System.out.println("2. Moje notifikacije");
            System.out.println("3. Označi notifikaciju kao pročitanu");
            System.out.println("0. Nazad na glavni meni");
            System.out.print("Izaberi opciju: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> sendNotification(apiClient, scanner);
                case "2" -> listMyNotifications(apiClient);
                case "3" -> markAsRead(apiClient, scanner);
                case "0" -> back = true;
                default -> System.out.println("Nepoznata opcija, pokušaj ponovo.");
            }
        }
    }

    /**
     * Šalje notifikaciju zadatom korisniku.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void sendNotification(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("ID korisnika kome se šalje notifikacija: ");
        String userId = scanner.nextLine().trim();
        System.out.print("Naslov notifikacije: ");
        String title = scanner.nextLine().trim();
        System.out.print("Poruka: ");
        String message = scanner.nextLine().trim();
        System.out.print("ID tipa notifikacije (1=SYSTEM, 2=ENROLLMENT, 3=COURSE, 4=CERTIFICATE, 5=REVIEW): ");
        String notificationTypeId = scanner.nextLine().trim();

        try {
            CreateNotificationRequest request = new CreateNotificationRequest(
                    Long.valueOf(userId), title, message, Long.valueOf(notificationTypeId));
            String jsonBody = JsonUtil.mapper().writeValueAsString(request);

            ApiResponse response = apiClient.post("/api/notifications", jsonBody);

            if (response.isSuccessful()) {
                NotificationDto notification = JsonUtil.mapper().readValue(response.body(), NotificationDto.class);
                System.out.println("Notifikacija uspešno poslata!");
                printNotificationDetails(notification);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (NumberFormatException e) {
            System.out.println("ID korisnika i ID tipa notifikacije moraju biti brojevi.");
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Preuzima i ispisuje sve notifikacije trenutno ulogovanog korisnika.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     */
    private static void listMyNotifications(ApiClient apiClient) {
        if (!requireLogin()) {
            return;
        }

        try {
            ApiResponse response = apiClient.get("/api/notifications/me");

            if (response.isSuccessful()) {
                List<NotificationDto> notifications = JsonUtil.mapper().readValue(response.body(), new TypeReference<List<NotificationDto>>() {
                });
                printNotificationList(notifications);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Označava zadatu notifikaciju kao pročitanu.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void markAsRead(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("ID notifikacije koja se označava kao pročitana: ");
        String id = scanner.nextLine().trim();

        try {
            ApiResponse response = apiClient.patch("/api/notifications/" + id + "/read", "");

            if (response.isSuccessful()) {
                NotificationDto notification = JsonUtil.mapper().readValue(response.body(), NotificationDto.class);
                System.out.println("Notifikacija označena kao pročitana.");
                printNotificationDetails(notification);
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
     * Ispisuje kratak pregled liste notifikacija (naslov, tip, pročitanost).
     *
     * @param notifications lista notifikacija za ispis.
     */
    private static void printNotificationList(List<NotificationDto> notifications) {
        if (notifications.isEmpty()) {
            System.out.println("Nemaš nijednu notifikaciju.");
            return;
        }

        System.out.println("Pronađeno notifikacija: " + notifications.size());
        for (NotificationDto notification : notifications) {
            System.out.println("  [" + notification.notificationId() + "] "
                    + (Boolean.TRUE.equals(notification.isRead()) ? "(pročitano) " : "(nepročitano) ")
                    + notification.notificationTitle() + " - " + notification.notificationTypeName());
        }
    }

    /**
     * Ispisuje pun detalj jedne notifikacije.
     *
     * @param notification notifikacija čiji se detalji ispisuju.
     */
    private static void printNotificationDetails(NotificationDto notification) {
        System.out.println("ID: " + notification.notificationId());
        System.out.println("Naslov: " + notification.notificationTitle());
        System.out.println("Poruka: " + notification.message());
        System.out.println("Tip: " + notification.notificationTypeName());
        System.out.println("Pročitano: " + notification.isRead());
        System.out.println("Poslato: " + notification.sentAt());
    }
}
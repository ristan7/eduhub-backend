package rs.ac.bg.fon.eduhub.client.menu;

import java.util.Scanner;
import rs.ac.bg.fon.eduhub.client.ApiClient;
import rs.ac.bg.fon.eduhub.client.ApiResponse;
import rs.ac.bg.fon.eduhub.client.JsonUtil;
import rs.ac.bg.fon.eduhub.client.SessionContext;
import rs.ac.bg.fon.eduhub.dto.AuthResponse;
import rs.ac.bg.fon.eduhub.dto.LoginRequest;
import rs.ac.bg.fon.eduhub.dto.RegisterRequest;
import rs.ac.bg.fon.eduhub.dto.UserDto;

/**
 * Podmeni konzolnog klijenta za operacije autentifikacije: registraciju,
 * prijavu i odjavu korisnika.
 */
public class AuthMenu {

    /**
     * Prikazuje podmeni za autentifikaciju u petlji, dok korisnik ne
     * izabere povratak na glavni meni.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    public static void show(ApiClient apiClient, Scanner scanner) {
        boolean back = false;

        while (!back) {
            System.out.println();
            System.out.println("----- Autentifikacija -----");
            System.out.println("1. Registracija");
            System.out.println("2. Prijava");
            System.out.println("3. Odjava");
            System.out.println("0. Nazad na glavni meni");
            System.out.print("Izaberi opciju: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> register(apiClient, scanner);
                case "2" -> login(apiClient, scanner);
                case "3" -> logout(apiClient);
                case "0" -> back = true;
                default -> System.out.println("Nepoznata opcija, pokušaj ponovo.");
            }
        }
    }

    /**
     * Prikuplja podatke o novom korisniku i šalje zahtev za registraciju.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void register(ApiClient apiClient, Scanner scanner) {
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Lozinka (min. 8 karaktera): ");
        String password = scanner.nextLine().trim();
        System.out.print("Ime: ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Prezime: ");
        String lastName = scanner.nextLine().trim();

        try {
            RegisterRequest request = new RegisterRequest(email, password, firstName, lastName);
            String jsonBody = JsonUtil.mapper().writeValueAsString(request);

            ApiResponse response = apiClient.post("/api/auth/register", jsonBody);

            if (response.isSuccessful()) {
                UserDto user = JsonUtil.mapper().readValue(response.body(), UserDto.class);
                System.out.println("Uspešna registracija! Dobrodošao/la, " + user.firstName() + " " + user.lastName() + ".");
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Prikuplja email i lozinku, prijavljuje korisnika i čuva JWT token
     * u {@link SessionContext} za dalje pozive.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void login(ApiClient apiClient, Scanner scanner) {
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Lozinka: ");
        String password = scanner.nextLine().trim();

        try {
            LoginRequest request = new LoginRequest(email, password);
            String jsonBody = JsonUtil.mapper().writeValueAsString(request);

            ApiResponse response = apiClient.post("/api/auth/login", jsonBody);

            if (response.isSuccessful()) {
                AuthResponse authResponse = JsonUtil.mapper().readValue(response.body(), AuthResponse.class);
                SessionContext.getInstance().login(authResponse.token(), authResponse.user().userEmail());
                System.out.println("Uspešna prijava! Ulogovan kao: " + authResponse.user().userEmail());
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Odjavljuje trenutnog korisnika: poziva backend endpoint za odjavu
     * (informativno, jer je autentifikacija bez stanja) i briše lokalnu sesiju.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     */
    private static void logout(ApiClient apiClient) {
        if (!SessionContext.getInstance().isLoggedIn()) {
            System.out.println("Nisi ni ulogovan/a.");
            return;
        }

        try {
            apiClient.post("/api/auth/logout", "");
        } catch (Exception e) {
            System.out.println("Napomena: odjava na serveru nije uspela (" + e.getMessage() + "), ali lokalna sesija je obrisana.");
        }

        SessionContext.getInstance().logout();
        System.out.println("Uspešno si odjavljen/a.");
    }
}
package rs.ac.bg.fon.eduhub.client.menu;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Scanner;
import rs.ac.bg.fon.eduhub.client.ApiClient;
import rs.ac.bg.fon.eduhub.client.ApiResponse;
import rs.ac.bg.fon.eduhub.client.JsonUtil;
import rs.ac.bg.fon.eduhub.client.SessionContext;
import rs.ac.bg.fon.eduhub.dto.CertificateDto;

/**
 * Podmeni konzolnog klijenta za izdavanje sertifikata i pregled
 * sopstvenih sertifikata.
 *
 * <p>Napomena: izdavanje sertifikata zahteva ulogu INSTRUCTOR ili ADMIN
 * (predavač potvrđuje da je student uspešno završio kurs).</p>
 */
public class CertificateMenu {

    /**
     * Prikazuje podmeni za sertifikate u petlji, dok korisnik ne izabere
     * povratak na glavni meni.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    public static void show(ApiClient apiClient, Scanner scanner) {
        boolean back = false;

        while (!back) {
            System.out.println();
            System.out.println("----- Sertifikati -----");
            System.out.println("1. Izdaj sertifikat (predavač/admin)");
            System.out.println("2. Moji sertifikati");
            System.out.println("0. Nazad na glavni meni");
            System.out.print("Izaberi opciju: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> issueCertificate(apiClient, scanner);
                case "2" -> listMyCertificates(apiClient);
                case "0" -> back = true;
                default -> System.out.println("Nepoznata opcija, pokušaj ponovo.");
            }
        }
    }

    /**
     * Izdaje sertifikat za zadatu prijavu.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void issueCertificate(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("ID prijave (enrollment) za koju se izdaje sertifikat: ");
        String enrollmentId = scanner.nextLine().trim();

        try {
            ApiResponse response = apiClient.post("/api/enrollments/" + enrollmentId + "/certificate", "");

            if (response.isSuccessful()) {
                CertificateDto certificate = JsonUtil.mapper().readValue(response.body(), CertificateDto.class);
                System.out.println("Sertifikat uspešno izdat!");
                printCertificateDetails(certificate);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Preuzima i ispisuje sve sertifikate trenutno ulogovanog korisnika.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     */
    private static void listMyCertificates(ApiClient apiClient) {
        if (!requireLogin()) {
            return;
        }

        try {
            ApiResponse response = apiClient.get("/api/certificates/me");

            if (response.isSuccessful()) {
                List<CertificateDto> certificates = JsonUtil.mapper().readValue(response.body(), new TypeReference<List<CertificateDto>>() {
                });
                printCertificateList(certificates);
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
     * Ispisuje kratak pregled liste sertifikata (kod, kurs, datum izdavanja).
     *
     * @param certificates lista sertifikata za ispis.
     */
    private static void printCertificateList(List<CertificateDto> certificates) {
        if (certificates.isEmpty()) {
            System.out.println("Nemaš nijedan sertifikat.");
            return;
        }

        System.out.println("Pronađeno sertifikata: " + certificates.size());
        for (CertificateDto certificate : certificates) {
            System.out.println("  [" + certificate.certificateId() + "] " + certificate.code()
                    + " - " + certificate.courseTitle() + " (izdat: " + certificate.issuedAt() + ")");
        }
    }

    /**
     * Ispisuje pun detalj jednog sertifikata.
     *
     * @param certificate sertifikat čiji se detalji ispisuju.
     */
    private static void printCertificateDetails(CertificateDto certificate) {
        System.out.println("ID: " + certificate.certificateId());
        System.out.println("Kod: " + certificate.code());
        System.out.println("Kurs: " + certificate.courseTitle());
        System.out.println("Student: " + certificate.studentFullName());
        System.out.println("Izdat: " + certificate.issuedAt());
        System.out.println("URL: " + (certificate.certificateUrl() != null ? certificate.certificateUrl() : "-"));
    }
}
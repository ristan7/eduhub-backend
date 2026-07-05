package rs.ac.bg.fon.eduhub.client.menu;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Scanner;
import rs.ac.bg.fon.eduhub.client.ApiClient;
import rs.ac.bg.fon.eduhub.client.ApiResponse;
import rs.ac.bg.fon.eduhub.client.JsonUtil;
import rs.ac.bg.fon.eduhub.client.SessionContext;
import rs.ac.bg.fon.eduhub.dto.CreateMaterialRequest;
import rs.ac.bg.fon.eduhub.dto.MaterialDto;

/**
 * Podmeni konzolnog klijenta za pregled i dodavanje nastavnih
 * materijala u okviru lekcije.
 *
 * <p>Napomena: identifikator tipa materijala ({@code materialTypeId}) se
 * unosi ručno; vrednosti se mogu proveriti u tabeli {@code material_type}
 * (1=PDF, 2=IMAGE, 3=LINK, 4=PRESENTATION, 5=VIDEO prema seed podacima).</p>
 */
public class MaterialMenu {

    /**
     * Prikazuje podmeni za materijale u petlji, dok korisnik ne izabere
     * povratak na glavni meni.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    public static void show(ApiClient apiClient, Scanner scanner) {
        boolean back = false;

        while (!back) {
            System.out.println();
            System.out.println("----- Materijali -----");
            System.out.println("1. Prikaz materijala lekcije (prijava obavezna)");
            System.out.println("2. Dodavanje materijala (prijava obavezna)");
            System.out.println("0. Nazad na glavni meni");
            System.out.print("Izaberi opciju: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> listMaterialsByLesson(apiClient, scanner);
                case "2" -> addMaterial(apiClient, scanner);
                case "0" -> back = true;
                default -> System.out.println("Nepoznata opcija, pokušaj ponovo.");
            }
        }
    }

    /**
     * Preuzima i ispisuje sve materijale zadate lekcije.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void listMaterialsByLesson(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("ID lekcije: ");
        String lessonId = scanner.nextLine().trim();

        try {
            ApiResponse response = apiClient.get("/api/lessons/" + lessonId + "/materials");

            if (response.isSuccessful()) {
                List<MaterialDto> materials = JsonUtil.mapper().readValue(response.body(), new TypeReference<List<MaterialDto>>() {
                });
                printMaterialList(materials);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (Exception e) {
            System.out.println("Greška pri komunikaciji sa serverom: " + e.getMessage());
        }
    }

    /**
     * Dodaje novi nastavni materijal na zadatu lekciju. Sadržaj i URL su
     * opcioni — prazan unos se šalje kao {@code null}.
     *
     * @param apiClient klijent za komunikaciju sa backend API-jem.
     * @param scanner   deljeni {@link Scanner} za unos sa konzole.
     */
    private static void addMaterial(ApiClient apiClient, Scanner scanner) {
        if (!requireLogin()) {
            return;
        }

        System.out.print("ID lekcije na koju se dodaje materijal: ");
        String lessonId = scanner.nextLine().trim();
        System.out.print("Naziv materijala: ");
        String name = scanner.nextLine().trim();
        System.out.print("Redni broj materijala (npr. 1, 2, 3...): ");
        String orderIndex = scanner.nextLine().trim();
        System.out.print("Tekstualni sadržaj (Enter za preskakanje): ");
        String content = scanner.nextLine().trim();
        System.out.print("URL adresa (Enter za preskakanje): ");
        String url = scanner.nextLine().trim();
        System.out.print("ID tipa materijala (1=PDF, 2=IMAGE, 3=LINK, 4=PRESENTATION, 5=VIDEO): ");
        String materialTypeId = scanner.nextLine().trim();

        try {
            CreateMaterialRequest request = new CreateMaterialRequest(
                    name,
                    Integer.valueOf(orderIndex),
                    content.isBlank() ? null : content,
                    url.isBlank() ? null : url,
                    Long.valueOf(materialTypeId));
            String jsonBody = JsonUtil.mapper().writeValueAsString(request);

            ApiResponse response = apiClient.post("/api/lessons/" + lessonId + "/materials", jsonBody);

            if (response.isSuccessful()) {
                MaterialDto material = JsonUtil.mapper().readValue(response.body(), MaterialDto.class);
                System.out.println("Materijal uspešno dodat!");
                printMaterialDetails(material);
            } else {
                JsonUtil.printError(response.body());
            }
        } catch (NumberFormatException e) {
            System.out.println("Redni broj i ID tipa materijala moraju biti brojevi.");
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
     * Ispisuje kratak pregled liste materijala (ID, redni broj i naziv).
     *
     * @param materials lista materijala za ispis.
     */
    private static void printMaterialList(List<MaterialDto> materials) {
        if (materials.isEmpty()) {
            System.out.println("Nema materijala za prikaz.");
            return;
        }

        System.out.println("Pronađeno materijala: " + materials.size());
        for (MaterialDto material : materials) {
            System.out.println("  [" + material.materialId() + "] #" + material.materialOrderIndex() + " "
                    + material.materialName() + " (tip: " + material.materialTypeName() + ")");
        }
    }

    /**
     * Ispisuje pun detalj jednog materijala.
     *
     * @param material materijal čiji se detalji ispisuju.
     */
    private static void printMaterialDetails(MaterialDto material) {
        System.out.println("ID: " + material.materialId());
        System.out.println("Naziv: " + material.materialName());
        System.out.println("Redni broj: " + material.materialOrderIndex());
        System.out.println("Tip: " + material.materialTypeName());
        System.out.println("Sadržaj: " + (material.content() != null ? material.content() : "-"));
        System.out.println("URL: " + (material.url() != null ? material.url() : "-"));
        System.out.println("ID lekcije: " + material.lessonId());
        System.out.println("Dodat: " + material.uploadedAt());
    }
}
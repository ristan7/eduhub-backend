package rs.ac.bg.fon.eduhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Zahtev za slanje notifikacije korisniku (SO25).
 *
 * @param userId identifikator korisnika kome se notifikacija šalje
 * @param notificationTitle naslov notifikacije
 * @param message tekst poruke notifikacije
 * @param notificationTypeId identifikator tipa notifikacije
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record CreateNotificationRequest(
        @NotNull Long userId,
        @NotBlank @Size(max = 150) String notificationTitle,
        @NotBlank String message,
        @NotNull Long notificationTypeId
) {
}
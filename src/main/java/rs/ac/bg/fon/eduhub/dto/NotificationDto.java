package rs.ac.bg.fon.eduhub.dto;

import java.time.LocalDateTime;

/**
 * Prenosni objekat (DTO) koji predstavlja podatke o notifikaciji vraćene
 * klijentu.
 *
 * @param notificationId jedinstveni identifikator notifikacije
 * @param notificationTitle naslov notifikacije
 * @param message tekst poruke notifikacije
 * @param sentAt datum i vreme slanja notifikacije
 * @param isRead da li je korisnik pročitao notifikaciju
 * @param userId identifikator korisnika kome je notifikacija upućena
 * @param notificationTypeId identifikator tipa notifikacije
 * @param notificationTypeName naziv tipa notifikacije
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record NotificationDto(
        Long notificationId,
        String notificationTitle,
        String message,
        LocalDateTime sentAt,
        Boolean isRead,
        Long userId,
        Long notificationTypeId,
        String notificationTypeName
) {
}
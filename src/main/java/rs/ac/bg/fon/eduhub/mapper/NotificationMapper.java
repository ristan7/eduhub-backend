package rs.ac.bg.fon.eduhub.mapper;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.eduhub.dto.NotificationDto;
import rs.ac.bg.fon.eduhub.entity.impl.Notification;

/**
 * Konvertuje {@link Notification} entitete u odgovarajuće {@link NotificationDto} objekte.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Component
public class NotificationMapper {

    /**
     * Mapira entitet notifikacije u DTO za slanje klijentu.
     *
     * @param notification entitet notifikacije, može biti {@code null}
     * @return {@link NotificationDto} sa podacima notifikacije, ili {@code null} ako je ulazni entitet {@code null}
     */
    public NotificationDto toDto(Notification notification) {
        if (notification == null) {
            return null;
        }
        return new NotificationDto(
                notification.getNotificationId(),
                notification.getNotificationTitle(),
                notification.getMessage(),
                notification.getSentAt(),
                notification.getIsRead(),
                notification.getUser() != null ? notification.getUser().getUserId() : null,
                notification.getNotificationType() != null ? notification.getNotificationType().getNotificationTypeId() : null,
                notification.getNotificationType() != null ? notification.getNotificationType().getNotificationTypeName() : null
        );
    }
}
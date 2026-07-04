package rs.ac.bg.fon.eduhub.mapper;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.eduhub.dto.NotificationDto;
import rs.ac.bg.fon.eduhub.entity.impl.Notification;

@Component
public class NotificationMapper {

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
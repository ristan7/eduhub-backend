package rs.ac.bg.fon.eduhub.dto;

import java.time.LocalDateTime;

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
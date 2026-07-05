package rs.ac.bg.fon.eduhub.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rs.ac.bg.fon.eduhub.dto.NotificationDto;
import rs.ac.bg.fon.eduhub.entity.impl.Notification;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.NotificationType;

/**
 * JUnit testovi za {@link NotificationMapper}.
 *
 * @author Mihajlo Ristanovic
 */
class NotificationMapperTest {

    private NotificationMapper notificationMapper;

    @BeforeEach
    void setUp() {
        notificationMapper = new NotificationMapper();
    }

    @Test
    void testToDtoMapsAllFields() {
        User user = new User();
        user.setUserId(1L);

        Notification notification = new Notification();
        notification.setNotificationId(1L);
        notification.setNotificationTitle("Naslov");
        notification.setMessage("Poruka");
        notification.setIsRead(false);
        notification.setUser(user);
        notification.setNotificationType(new NotificationType(1L, "SYSTEM"));

        NotificationDto dto = notificationMapper.toDto(notification);

        assertEquals(1L, dto.notificationId());
        assertEquals("Naslov", dto.notificationTitle());
        assertEquals("Poruka", dto.message());
        assertEquals(false, dto.isRead());
        assertEquals(1L, dto.userId());
        assertEquals("SYSTEM", dto.notificationTypeName());
    }

    @Test
    void testToDtoWithNullAssociationsReturnsNullFields() {
        Notification notification = new Notification();
        notification.setNotificationId(1L);

        NotificationDto dto = notificationMapper.toDto(notification);

        assertNull(dto.userId());
        assertNull(dto.notificationTypeId());
    }

    @Test
    void testToDtoWithNullNotificationReturnsNull() {
        assertNull(notificationMapper.toDto(null));
    }
}
package rs.ac.bg.fon.eduhub.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit testovi za entitet {@link Notification}.
 *
 * @author Mihajlo Ristanovic
 */
class NotificationTest {

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = new Notification();
    }

    @Test
    void testGettersAndSetters() {
        notification.setNotificationId(1L);
        notification.setNotificationTitle("Sertifikat izdat");
        notification.setMessage("Vas sertifikat je spreman.");
        notification.setIsRead(false);

        assertEquals(1L, notification.getNotificationId());
        assertEquals("Sertifikat izdat", notification.getNotificationTitle());
        assertEquals("Vas sertifikat je spreman.", notification.getMessage());
        assertFalse(notification.getIsRead());
    }

    @Test
    void testEqualsSameId() {
        Notification first = new Notification();
        first.setNotificationId(1L);

        Notification second = new Notification();
        second.setNotificationId(1L);

        assertEquals(first, second);
    }

    @Test
    void testEqualsDifferentId() {
        Notification first = new Notification();
        first.setNotificationId(1L);

        Notification second = new Notification();
        second.setNotificationId(2L);

        assertNotEquals(first, second);
    }

    @Test
    void testOnCreateSetsSentAt() {
        assertEquals(null, notification.getSentAt());
        notification.onCreate();
        assertNotNull(notification.getSentAt());
    }

    @Test
    void testDefaultIsReadIsFalse() {
        Notification fresh = new Notification();
        assertFalse(fresh.getIsRead());
    }
}
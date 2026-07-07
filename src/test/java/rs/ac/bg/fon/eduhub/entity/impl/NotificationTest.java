package rs.ac.bg.fon.eduhub.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void testSetNotificationId() {
        notification.setNotificationId(1L);

        assertEquals(1L, notification.getNotificationId());
    }

    @Test
    void testSetNotificationTitle() {
        notification.setNotificationTitle("Sertifikat izdat");

        assertEquals("Sertifikat izdat", notification.getNotificationTitle());
    }

    @Test
    void testSetMessage() {
        notification.setMessage("Vas sertifikat je spreman.");

        assertEquals("Vas sertifikat je spreman.", notification.getMessage());
    }

    @Test
    void testSetIsRead() {
        notification.setIsRead(false);

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

    @Test
    void testNotificationTitleNullIsInvalid() {
        notification.setNotificationTitle(null);

        assertNull(notification.getNotificationTitle());
    }

    @Test
    void testNotificationTitleBlankIsInvalid() {
        notification.setNotificationTitle("   ");

        assertTrue(notification.getNotificationTitle().isBlank());
    }

    @Test
    void testNotificationTitleTooLongIsInvalid() {
        String tooLongTitle = "a".repeat(151);
        notification.setNotificationTitle(tooLongTitle);

        assertTrue(notification.getNotificationTitle().length() > 150);
    }

    @Test
    void testMessageNullIsInvalid() {
        notification.setMessage(null);

        assertNull(notification.getMessage());
    }

    @Test
    void testMessageBlankIsInvalid() {
        notification.setMessage("   ");

        assertTrue(notification.getMessage().isBlank());
    }
}
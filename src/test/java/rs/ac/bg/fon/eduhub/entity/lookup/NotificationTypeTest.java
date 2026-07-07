package rs.ac.bg.fon.eduhub.entity.lookup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit testovi za entitet {@link NotificationType}.
 *
 * @author Mihajlo Ristanovic
 */
class NotificationTypeTest {

    private NotificationType notificationType;

    @BeforeEach
    void setUp() {
        notificationType = new NotificationType();
    }

    @Test
    void testSetNotificationTypeId() {
        notificationType.setNotificationTypeId(1L);

        assertEquals(1L, notificationType.getNotificationTypeId());
    }

    @Test
    void testSetNotificationTypeName() {
        notificationType.setNotificationTypeName("SYSTEM");

        assertEquals("SYSTEM", notificationType.getNotificationTypeName());
    }

    @Test
    void testEqualsSameId() {
        NotificationType first = new NotificationType(1L, "SYSTEM");
        NotificationType second = new NotificationType(1L, "CERTIFICATE");

        assertEquals(first, second);
    }

    @Test
    void testEqualsDifferentId() {
        NotificationType first = new NotificationType(1L, "SYSTEM");
        NotificationType second = new NotificationType(2L, "SYSTEM");

        assertNotEquals(first, second);
    }

    @Test
    void testHashCodeConsistentWithEquals() {
        NotificationType first = new NotificationType(1L, "SYSTEM");
        NotificationType second = new NotificationType(1L, "SYSTEM");

        assertEquals(first.hashCode(), second.hashCode());
    }
}
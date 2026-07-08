package rs.ac.bg.fon.eduhub.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit testovi za entitet {@link Notification}.
 *
 * @author Mihajlo Ristanovic
 */
class NotificationTest {

    private static Validator validator;

    private Notification notification;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        notification = validNotification();
    }

    private Notification validNotification() {
        Notification n = new Notification();
        n.setNotificationTitle("Sertifikat izdat");
        n.setMessage("Vas sertifikat je spreman.");
        return n;
    }

    private boolean hasViolationFor(Set<ConstraintViolation<Notification>> violations, String propertyName) {
        return violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals(propertyName));
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
    void testNotificationTitleValidPassesValidation() {
        notification.setNotificationTitle("Sertifikat izdat");

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);

        assertFalse(hasViolationFor(violations, "notificationTitle"));
    }

    @Test
    void testNotificationTitleNullFailsValidation() {
        notification.setNotificationTitle(null);

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);

        assertTrue(hasViolationFor(violations, "notificationTitle"));
    }

    @Test
    void testNotificationTitleEmptyFailsValidation() {
        notification.setNotificationTitle("");

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);

        assertTrue(hasViolationFor(violations, "notificationTitle"));
    }

    @Test
    void testNotificationTitleBlankFailsValidation() {
        notification.setNotificationTitle("   ");

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);

        assertTrue(hasViolationFor(violations, "notificationTitle"));
    }

    @Test
    void testNotificationTitleAtMaxLengthPassesValidation() {
        notification.setNotificationTitle("a".repeat(150));

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);

        assertFalse(hasViolationFor(violations, "notificationTitle"));
    }

    @Test
    void testNotificationTitleOverMaxLengthFailsValidation() {
        notification.setNotificationTitle("a".repeat(151));

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);

        assertTrue(hasViolationFor(violations, "notificationTitle"));
    }

    @Test
    void testMessageValidPassesValidation() {
        notification.setMessage("Vas sertifikat je spreman.");

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);

        assertFalse(hasViolationFor(violations, "message"));
    }

    @Test
    void testMessageNullFailsValidation() {
        notification.setMessage(null);

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);

        assertTrue(hasViolationFor(violations, "message"));
    }

    @Test
    void testMessageEmptyFailsValidation() {
        notification.setMessage("");

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);

        assertTrue(hasViolationFor(violations, "message"));
    }

    @Test
    void testMessageBlankFailsValidation() {
        notification.setMessage("   ");

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);

        assertTrue(hasViolationFor(violations, "message"));
    }
}
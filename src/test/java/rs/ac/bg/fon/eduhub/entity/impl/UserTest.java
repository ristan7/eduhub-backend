package rs.ac.bg.fon.eduhub.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rs.ac.bg.fon.eduhub.entity.lookup.Role;

/**
 * JUnit testovi za entitet {@link User}.
 *
 * @author Mihajlo Ristanovic
 */
class UserTest {

    private static Validator validator;

    private User user;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        user = validUser();
    }

    private User validUser() {
        User u = new User();
        u.setUserEmail("student@eduhub.com");
        u.setPassword("hashedPassword123");
        u.setFirstName("Petar");
        u.setLastName("Nikolic");
        return u;
    }

    private boolean hasViolationFor(Set<ConstraintViolation<User>> violations, String propertyName) {
        return violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals(propertyName));
    }

    @Test
    void testSetUserId() {
        user.setUserId(1L);

        assertEquals(1L, user.getUserId());
    }

    @Test
    void testSetUserEmail() {
        user.setUserEmail("student@eduhub.com");

        assertEquals("student@eduhub.com", user.getUserEmail());
    }

    @Test
    void testSetPassword() {
        user.setPassword("hashedPassword");

        assertEquals("hashedPassword", user.getPassword());
    }

    @Test
    void testSetFirstName() {
        user.setFirstName("Petar");

        assertEquals("Petar", user.getFirstName());
    }

    @Test
    void testSetLastName() {
        user.setLastName("Nikolic");

        assertEquals("Nikolic", user.getLastName());
    }

    @Test
    void testSetIsActive() {
        user.setIsActive(true);

        assertTrue(user.getIsActive());
    }

    @Test
    void testSetRole() {
        Role role = new Role(1L, "STUDENT");

        user.setRole(role);

        assertEquals(role, user.getRole());
    }

    @Test
    void testEqualsSameId() {
        User first = new User();
        first.setUserId(1L);
        first.setUserEmail("first@eduhub.com");

        User second = new User();
        second.setUserId(1L);
        second.setUserEmail("second@eduhub.com");

        assertEquals(first, second);
    }

    @Test
    void testEqualsDifferentId() {
        User first = new User();
        first.setUserId(1L);

        User second = new User();
        second.setUserId(2L);

        assertNotEquals(first, second);
    }

    @Test
    void testOnCreateSetsCreatedAt() {
        assertEquals(null, user.getCreatedAt());
        user.onCreate();
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void testDefaultCollectionsAreEmptyNotNull() {
        assertNotNull(user.getCourses());
        assertTrue(user.getCourses().isEmpty());
        assertNotNull(user.getEnrollments());
        assertTrue(user.getEnrollments().isEmpty());
        assertNotNull(user.getNotifications());
        assertTrue(user.getNotifications().isEmpty());
    }

    @Test
    void testUserEmailValidPassesValidation() {
        user.setUserEmail("student@eduhub.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(hasViolationFor(violations, "userEmail"));
    }

    @Test
    void testUserEmailNullFailsValidation() {
        user.setUserEmail(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "userEmail"));
    }

    @Test
    void testUserEmailEmptyFailsValidation() {
        user.setUserEmail("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "userEmail"));
    }

    @Test
    void testUserEmailBlankFailsValidation() {
        user.setUserEmail("   ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "userEmail"));
    }

    @Test
    void testUserEmailInvalidFormatFailsValidation() {
        user.setUserEmail("nije-email-adresa");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "userEmail"));
    }

    @Test
    void testUserEmailOverMaxLengthFailsValidation() {
        String tooLongEmail = "a".repeat(140) + "@eduhub.com";
        user.setUserEmail(tooLongEmail);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(151, tooLongEmail.length());
        assertTrue(hasViolationFor(violations, "userEmail"));
    }

    @Test
    void testPasswordValidPassesValidation() {
        user.setPassword("hashedPassword123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(hasViolationFor(violations, "password"));
    }

    @Test
    void testPasswordNullFailsValidation() {
        user.setPassword(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "password"));
    }

    @Test
    void testPasswordEmptyFailsValidation() {
        user.setPassword("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "password"));
    }

    @Test
    void testPasswordBlankFailsValidation() {
        user.setPassword("   ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "password"));
    }

    @Test
    void testPasswordAtMaxLengthPassesValidation() {
        user.setPassword("a".repeat(255));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(hasViolationFor(violations, "password"));
    }

    @Test
    void testPasswordOverMaxLengthFailsValidation() {
        user.setPassword("a".repeat(256));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "password"));
    }

    @Test
    void testFirstNameValidPassesValidation() {
        user.setFirstName("Petar");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(hasViolationFor(violations, "firstName"));
    }

    @Test
    void testFirstNameNullFailsValidation() {
        user.setFirstName(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "firstName"));
    }

    @Test
    void testFirstNameEmptyFailsValidation() {
        user.setFirstName("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "firstName"));
    }

    @Test
    void testFirstNameBlankFailsValidation() {
        user.setFirstName("   ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "firstName"));
    }

    @Test
    void testFirstNameAtMaxLengthPassesValidation() {
        user.setFirstName("a".repeat(60));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(hasViolationFor(violations, "firstName"));
    }

    @Test
    void testFirstNameOverMaxLengthFailsValidation() {
        user.setFirstName("a".repeat(61));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "firstName"));
    }

    @Test
    void testLastNameValidPassesValidation() {
        user.setLastName("Nikolic");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(hasViolationFor(violations, "lastName"));
    }

    @Test
    void testLastNameNullFailsValidation() {
        user.setLastName(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "lastName"));
    }

    @Test
    void testLastNameEmptyFailsValidation() {
        user.setLastName("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "lastName"));
    }

    @Test
    void testLastNameBlankFailsValidation() {
        user.setLastName("   ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "lastName"));
    }

    @Test
    void testLastNameAtMaxLengthPassesValidation() {
        user.setLastName("a".repeat(60));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(hasViolationFor(violations, "lastName"));
    }

    @Test
    void testLastNameOverMaxLengthFailsValidation() {
        user.setLastName("a".repeat(61));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(hasViolationFor(violations, "lastName"));
    }
}
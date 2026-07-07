package rs.ac.bg.fon.eduhub.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rs.ac.bg.fon.eduhub.entity.lookup.Role;

/**
 * JUnit testovi za entitet {@link User}.
 *
 * @author Mihajlo Ristanovic
 */
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
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
    void testUserEmailNullIsInvalid() {
        user.setUserEmail(null);

        assertNull(user.getUserEmail());
    }

    @Test
    void testUserEmailBlankIsInvalid() {
        user.setUserEmail("   ");

        assertTrue(user.getUserEmail().isBlank());
    }

    @Test
    void testUserEmailInvalidFormatIsInvalid() {
        user.setUserEmail("nije-email-adresa");

        assertFalse(user.getUserEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"));
    }

    @Test
    void testPasswordNullIsInvalid() {
        user.setPassword(null);

        assertNull(user.getPassword());
    }

    @Test
    void testPasswordBlankIsInvalid() {
        user.setPassword("   ");

        assertTrue(user.getPassword().isBlank());
    }

    @Test
    void testFirstNameNullIsInvalid() {
        user.setFirstName(null);

        assertNull(user.getFirstName());
    }

    @Test
    void testFirstNameBlankIsInvalid() {
        user.setFirstName("   ");

        assertTrue(user.getFirstName().isBlank());
    }

    @Test
    void testLastNameNullIsInvalid() {
        user.setLastName(null);

        assertNull(user.getLastName());
    }

    @Test
    void testLastNameBlankIsInvalid() {
        user.setLastName("   ");

        assertTrue(user.getLastName().isBlank());
    }
}
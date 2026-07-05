package rs.ac.bg.fon.eduhub.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void testGettersAndSetters() {
        Role role = new Role(1L, "STUDENT");

        user.setUserId(1L);
        user.setUserEmail("student@eduhub.com");
        user.setPassword("hashedPassword");
        user.setFirstName("Petar");
        user.setLastName("Nikolic");
        user.setIsActive(true);
        user.setRole(role);

        assertEquals(1L, user.getUserId());
        assertEquals("student@eduhub.com", user.getUserEmail());
        assertEquals("hashedPassword", user.getPassword());
        assertEquals("Petar", user.getFirstName());
        assertEquals("Nikolic", user.getLastName());
        assertTrue(user.getIsActive());
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
}
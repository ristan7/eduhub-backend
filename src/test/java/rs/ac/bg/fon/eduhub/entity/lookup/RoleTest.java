package rs.ac.bg.fon.eduhub.entity.lookup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit testovi za entitet {@link Role}.
 *
 * @author Mihajlo Ristanovic
 */
class RoleTest {

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
    }

    @Test
    void testSetRoleId() {
        role.setRoleId(1L);

        assertEquals(1L, role.getRoleId());
    }

    @Test
    void testSetRoleName() {
        role.setRoleName("INSTRUCTOR");

        assertEquals("INSTRUCTOR", role.getRoleName());
    }

    @Test
    void testEqualsSameId() {
        Role first = new Role(1L, "STUDENT");
        Role second = new Role(1L, "ADMIN");

        assertEquals(first, second, "Uloge sa istim ID-jem treba da budu jednake, bez obzira na naziv");
    }

    @Test
    void testEqualsDifferentId() {
        Role first = new Role(1L, "STUDENT");
        Role second = new Role(2L, "STUDENT");

        assertNotEquals(first, second, "Uloge sa različitim ID-jem ne treba da budu jednake");
    }

    @Test
    void testHashCodeConsistentWithEquals() {
        Role first = new Role(1L, "STUDENT");
        Role second = new Role(1L, "STUDENT");

        assertEquals(first.hashCode(), second.hashCode());
    }
}
package rs.ac.bg.fon.eduhub.entity.lookup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit testovi za entitet {@link EnrollmentStatus}.
 *
 * @author Mihajlo Ristanovic
 */
class EnrollmentStatusTest {

    private EnrollmentStatus enrollmentStatus;

    @BeforeEach
    void setUp() {
        enrollmentStatus = new EnrollmentStatus();
    }

    @Test
    void testGettersAndSetters() {
        enrollmentStatus.setEnrollmentStatusId(1L);
        enrollmentStatus.setEnrollmentStatusName("ACTIVE");

        assertEquals(1L, enrollmentStatus.getEnrollmentStatusId());
        assertEquals("ACTIVE", enrollmentStatus.getEnrollmentStatusName());
    }

    @Test
    void testEqualsSameId() {
        EnrollmentStatus first = new EnrollmentStatus(1L, "ACTIVE");
        EnrollmentStatus second = new EnrollmentStatus(1L, "COMPLETED");

        assertEquals(first, second);
    }

    @Test
    void testEqualsDifferentId() {
        EnrollmentStatus first = new EnrollmentStatus(1L, "ACTIVE");
        EnrollmentStatus second = new EnrollmentStatus(2L, "ACTIVE");

        assertNotEquals(first, second);
    }

    @Test
    void testHashCodeConsistentWithEquals() {
        EnrollmentStatus first = new EnrollmentStatus(1L, "ACTIVE");
        EnrollmentStatus second = new EnrollmentStatus(1L, "ACTIVE");

        assertEquals(first.hashCode(), second.hashCode());
    }
}
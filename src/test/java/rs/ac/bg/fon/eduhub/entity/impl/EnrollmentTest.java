package rs.ac.bg.fon.eduhub.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit testovi za entitet {@link Enrollment}.
 *
 * @author Mihajlo Ristanovic
 */
class EnrollmentTest {

    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        enrollment = new Enrollment();
    }

    @Test
    void testGettersAndSetters() {
        enrollment.setEnrollmentId(1L);
        enrollment.setProgressPercentage(50);

        assertEquals(1L, enrollment.getEnrollmentId());
        assertEquals(50, enrollment.getProgressPercentage());
    }

    @Test
    void testEqualsSameId() {
        Enrollment first = new Enrollment();
        first.setEnrollmentId(1L);

        Enrollment second = new Enrollment();
        second.setEnrollmentId(1L);

        assertEquals(first, second);
    }

    @Test
    void testEqualsDifferentId() {
        Enrollment first = new Enrollment();
        first.setEnrollmentId(1L);

        Enrollment second = new Enrollment();
        second.setEnrollmentId(2L);

        assertNotEquals(first, second);
    }

    @Test
    void testOnCreateSetsEnrolledAtWhenNull() {
        assertEquals(null, enrollment.getEnrolledAt());
        enrollment.onCreate();
        assertNotNull(enrollment.getEnrolledAt());
    }

    @Test
    void testOnCreateDoesNotOverwriteExistingEnrolledAt() {
        java.time.LocalDateTime fixedTime = java.time.LocalDateTime.of(2026, 1, 1, 12, 0);
        enrollment.setEnrolledAt(fixedTime);

        enrollment.onCreate();

        assertEquals(fixedTime, enrollment.getEnrolledAt());
    }

    @Test
    void testDefaultProgressPercentageIsZero() {
        Enrollment fresh = new Enrollment();
        assertEquals(0, fresh.getProgressPercentage());
    }
}
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

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit testovi za entitet {@link Enrollment}.
 *
 * @author Mihajlo Ristanovic
 */
class EnrollmentTest {

    private static Validator validator;

    private Enrollment enrollment;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        enrollment = validEnrollment();
    }

    private Enrollment validEnrollment() {
        Enrollment e = new Enrollment();
        e.setProgressPercentage(50);
        return e;
    }

    private boolean hasViolationFor(Set<ConstraintViolation<Enrollment>> violations, String propertyName) {
        return violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals(propertyName));
    }

    @Test
    void testSetEnrollmentId() {
        enrollment.setEnrollmentId(1L);

        assertEquals(1L, enrollment.getEnrollmentId());
    }

    @Test
    void testSetProgressPercentage() {
        enrollment.setProgressPercentage(50);

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
        LocalDateTime fixedTime = LocalDateTime.of(2026, 1, 1, 12, 0);
        enrollment.setEnrolledAt(fixedTime);

        enrollment.onCreate();

        assertEquals(fixedTime, enrollment.getEnrolledAt());
    }

    @Test
    void testDefaultProgressPercentageIsZero() {
        Enrollment fresh = new Enrollment();
        assertEquals(0, fresh.getProgressPercentage());
    }

    @Test
    void testProgressPercentageValidPassesValidation() {
        enrollment.setProgressPercentage(50);

        Set<ConstraintViolation<Enrollment>> violations = validator.validate(enrollment);

        assertFalse(hasViolationFor(violations, "progressPercentage"));
    }

    @Test
    void testProgressPercentageNullFailsValidation() {
        enrollment.setProgressPercentage(null);

        Set<ConstraintViolation<Enrollment>> violations = validator.validate(enrollment);

        assertTrue(hasViolationFor(violations, "progressPercentage"));
    }

    @Test
    void testProgressPercentageBelowMinimumFailsValidation() {
        enrollment.setProgressPercentage(-1);

        Set<ConstraintViolation<Enrollment>> violations = validator.validate(enrollment);

        assertTrue(hasViolationFor(violations, "progressPercentage"));
    }

    @Test
    void testProgressPercentageAtMinimumPassesValidation() {
        enrollment.setProgressPercentage(0);

        Set<ConstraintViolation<Enrollment>> violations = validator.validate(enrollment);

        assertFalse(hasViolationFor(violations, "progressPercentage"));
    }

    @Test
    void testProgressPercentageAtMaximumPassesValidation() {
        enrollment.setProgressPercentage(100);

        Set<ConstraintViolation<Enrollment>> violations = validator.validate(enrollment);

        assertFalse(hasViolationFor(violations, "progressPercentage"));
    }

    @Test
    void testProgressPercentageAboveMaximumFailsValidation() {
        enrollment.setProgressPercentage(101);

        Set<ConstraintViolation<Enrollment>> violations = validator.validate(enrollment);

        assertTrue(hasViolationFor(violations, "progressPercentage"));
    }
}
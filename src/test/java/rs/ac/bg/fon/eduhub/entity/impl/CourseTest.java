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

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit testovi za entitet {@link Course}.
 *
 * @author Mihajlo Ristanovic
 */
class CourseTest {

    private static Validator validator;

    private Course course;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        course = validCourse();
    }

    private Course validCourse() {
        Course c = new Course();
        c.setCourseTitle("Java Osnove");
        c.setCourseDescription("Uvod u Javu");
        return c;
    }

    private boolean hasViolationFor(Set<ConstraintViolation<Course>> violations, String propertyName) {
        return violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals(propertyName));
    }

    @Test
    void testSetCourseId() {
        course.setCourseId(1L);

        assertEquals(1L, course.getCourseId());
    }

    @Test
    void testSetCourseTitle() {
        course.setCourseTitle("Java Osnove");

        assertEquals("Java Osnove", course.getCourseTitle());
    }

    @Test
    void testSetCourseDescription() {
        course.setCourseDescription("Uvod u Javu");

        assertEquals("Uvod u Javu", course.getCourseDescription());
    }

    @Test
    void testSetIsPublished() {
        course.setIsPublished(true);

        assertTrue(course.getIsPublished());
    }

    @Test
    void testEqualsSameId() {
        Course first = new Course();
        first.setCourseId(1L);

        Course second = new Course();
        second.setCourseId(1L);

        assertEquals(first, second);
    }

    @Test
    void testEqualsDifferentId() {
        Course first = new Course();
        first.setCourseId(1L);

        Course second = new Course();
        second.setCourseId(2L);

        assertNotEquals(first, second);
    }

    @Test
    void testOnCreateSetsCreatedAtAndUpdatedAt() {
        course.onCreate();

        assertNotNull(course.getCreatedAt());
        assertNotNull(course.getUpdatedAt());
        assertEquals(course.getCreatedAt(), course.getUpdatedAt());
    }

    @Test
    void testOnUpdateChangesUpdatedAt() throws InterruptedException {
        course.onCreate();
        LocalDateTime originalUpdatedAt = course.getUpdatedAt();

        Thread.sleep(10);
        course.onUpdate();

        assertTrue(course.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void testDefaultCollectionsAreEmptyNotNull() {
        assertNotNull(course.getLessons());
        assertTrue(course.getLessons().isEmpty());
        assertNotNull(course.getEnrollments());
        assertTrue(course.getEnrollments().isEmpty());
    }


    @Test
    void testCourseTitleValidPassesValidation() {
        course.setCourseTitle("Java Osnove");

        Set<ConstraintViolation<Course>> violations = validator.validate(course);

        assertFalse(hasViolationFor(violations, "courseTitle"));
    }

    @Test
    void testCourseTitleNullFailsValidation() {
        course.setCourseTitle(null);

        Set<ConstraintViolation<Course>> violations = validator.validate(course);

        assertTrue(hasViolationFor(violations, "courseTitle"));
    }

    @Test
    void testCourseTitleEmptyFailsValidation() {
        course.setCourseTitle("");

        Set<ConstraintViolation<Course>> violations = validator.validate(course);

        assertTrue(hasViolationFor(violations, "courseTitle"));
    }

    @Test
    void testCourseTitleBlankFailsValidation() {
        course.setCourseTitle("   ");

        Set<ConstraintViolation<Course>> violations = validator.validate(course);

        assertTrue(hasViolationFor(violations, "courseTitle"));
    }

    @Test
    void testCourseTitleAtMaxLengthPassesValidation() {
        course.setCourseTitle("a".repeat(150));

        Set<ConstraintViolation<Course>> violations = validator.validate(course);

        assertFalse(hasViolationFor(violations, "courseTitle"));
    }

    @Test
    void testCourseTitleOverMaxLengthFailsValidation() {
        course.setCourseTitle("a".repeat(151));

        Set<ConstraintViolation<Course>> violations = validator.validate(course);

        assertTrue(hasViolationFor(violations, "courseTitle"));
    }

    @Test
    void testCourseDescriptionValidPassesValidation() {
        course.setCourseDescription("Uvod u Javu");

        Set<ConstraintViolation<Course>> violations = validator.validate(course);

        assertFalse(hasViolationFor(violations, "courseDescription"));
    }

    @Test
    void testCourseDescriptionNullFailsValidation() {
        course.setCourseDescription(null);

        Set<ConstraintViolation<Course>> violations = validator.validate(course);

        assertTrue(hasViolationFor(violations, "courseDescription"));
    }

    @Test
    void testCourseDescriptionEmptyFailsValidation() {
        course.setCourseDescription("");

        Set<ConstraintViolation<Course>> violations = validator.validate(course);

        assertTrue(hasViolationFor(violations, "courseDescription"));
    }

    @Test
    void testCourseDescriptionBlankFailsValidation() {
        course.setCourseDescription("   ");

        Set<ConstraintViolation<Course>> violations = validator.validate(course);

        assertTrue(hasViolationFor(violations, "courseDescription"));
    }
}
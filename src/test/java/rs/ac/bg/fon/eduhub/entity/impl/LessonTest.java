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

/**
 * JUnit testovi za entitet {@link Lesson}.
 *
 * @author Mihajlo Ristanovic
 */
class LessonTest {

    private static Validator validator;

    private Lesson lesson;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        lesson = validLesson();
    }

    private Lesson validLesson() {
        Lesson l = new Lesson();
        l.setLessonTitle("Uvod u promenljive");
        l.setLessonOrderIndex(1);
        return l;
    }

    private boolean hasViolationFor(Set<ConstraintViolation<Lesson>> violations, String propertyName) {
        return violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals(propertyName));
    }

    @Test
    void testSetLessonId() {
        lesson.setLessonId(1L);

        assertEquals(1L, lesson.getLessonId());
    }

    @Test
    void testSetLessonTitle() {
        lesson.setLessonTitle("Uvod u promenljive");

        assertEquals("Uvod u promenljive", lesson.getLessonTitle());
    }

    @Test
    void testSetLessonOrderIndex() {
        lesson.setLessonOrderIndex(1);

        assertEquals(1, lesson.getLessonOrderIndex());
    }

    @Test
    void testSetIsAvailable() {
        lesson.setIsAvailable(true);

        assertTrue(lesson.getIsAvailable());
    }

    @Test
    void testEqualsSameId() {
        Lesson first = new Lesson();
        first.setLessonId(1L);

        Lesson second = new Lesson();
        second.setLessonId(1L);

        assertEquals(first, second);
    }

    @Test
    void testEqualsDifferentId() {
        Lesson first = new Lesson();
        first.setLessonId(1L);

        Lesson second = new Lesson();
        second.setLessonId(2L);

        assertNotEquals(first, second);
    }

    @Test
    void testOnCreateSetsCreatedAt() {
        assertEquals(null, lesson.getCreatedAt());
        lesson.onCreate();
        assertNotNull(lesson.getCreatedAt());
    }

    @Test
    void testDefaultMaterialsListIsEmptyNotNull() {
        assertNotNull(lesson.getMaterials());
        assertTrue(lesson.getMaterials().isEmpty());
    }

    @Test
    void testLessonTitleValidPassesValidation() {
        lesson.setLessonTitle("Uvod u promenljive");

        Set<ConstraintViolation<Lesson>> violations = validator.validate(lesson);

        assertFalse(hasViolationFor(violations, "lessonTitle"));
    }

    @Test
    void testLessonTitleNullFailsValidation() {
        lesson.setLessonTitle(null);

        Set<ConstraintViolation<Lesson>> violations = validator.validate(lesson);

        assertTrue(hasViolationFor(violations, "lessonTitle"));
    }

    @Test
    void testLessonTitleEmptyFailsValidation() {
        lesson.setLessonTitle("");

        Set<ConstraintViolation<Lesson>> violations = validator.validate(lesson);

        assertTrue(hasViolationFor(violations, "lessonTitle"));
    }

    @Test
    void testLessonTitleBlankFailsValidation() {
        lesson.setLessonTitle("   ");

        Set<ConstraintViolation<Lesson>> violations = validator.validate(lesson);

        assertTrue(hasViolationFor(violations, "lessonTitle"));
    }

    @Test
    void testLessonTitleAtMaxLengthPassesValidation() {
        lesson.setLessonTitle("a".repeat(150));

        Set<ConstraintViolation<Lesson>> violations = validator.validate(lesson);

        assertFalse(hasViolationFor(violations, "lessonTitle"));
    }

    @Test
    void testLessonTitleOverMaxLengthFailsValidation() {
        lesson.setLessonTitle("a".repeat(151));

        Set<ConstraintViolation<Lesson>> violations = validator.validate(lesson);

        assertTrue(hasViolationFor(violations, "lessonTitle"));
    }

    @Test
    void testLessonOrderIndexValidPassesValidation() {
        lesson.setLessonOrderIndex(1);

        Set<ConstraintViolation<Lesson>> violations = validator.validate(lesson);

        assertFalse(hasViolationFor(violations, "lessonOrderIndex"));
    }

    @Test
    void testLessonOrderIndexNullFailsValidation() {
        lesson.setLessonOrderIndex(null);

        Set<ConstraintViolation<Lesson>> violations = validator.validate(lesson);

        assertTrue(hasViolationFor(violations, "lessonOrderIndex"));
    }
}
package rs.ac.bg.fon.eduhub.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit testovi za entitet {@link Lesson}.
 *
 * @author Mihajlo Ristanovic
 */
class LessonTest {

    private Lesson lesson;

    @BeforeEach
    void setUp() {
        lesson = new Lesson();
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
    void testLessonTitleNullIsInvalid() {
        lesson.setLessonTitle(null);

        assertNull(lesson.getLessonTitle());
    }

    @Test
    void testLessonTitleBlankIsInvalid() {
        lesson.setLessonTitle("   ");

        assertTrue(lesson.getLessonTitle().isBlank());
    }

    @Test
    void testLessonTitleTooLongIsInvalid() {
        String tooLongTitle = "a".repeat(151);
        lesson.setLessonTitle(tooLongTitle);

        assertTrue(lesson.getLessonTitle().length() > 150);
    }

    @Test
    void testLessonOrderIndexNullIsInvalid() {
        lesson.setLessonOrderIndex(null);

        assertNull(lesson.getLessonOrderIndex());
    }
}
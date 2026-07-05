package rs.ac.bg.fon.eduhub.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void testGettersAndSetters() {
        lesson.setLessonId(1L);
        lesson.setLessonTitle("Uvod u promenljive");
        lesson.setLessonOrderIndex(1);
        lesson.setIsAvailable(true);

        assertEquals(1L, lesson.getLessonId());
        assertEquals("Uvod u promenljive", lesson.getLessonTitle());
        assertEquals(1, lesson.getLessonOrderIndex());
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
}
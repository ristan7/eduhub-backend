package rs.ac.bg.fon.eduhub.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit testovi za entitet {@link Course}.
 *
 * @author Mihajlo Ristanovic
 */
class CourseTest {

    private Course course;

    @BeforeEach
    void setUp() {
        course = new Course();
    }

    @Test
    void testGettersAndSetters() {
        course.setCourseId(1L);
        course.setCourseTitle("Java Osnove");
        course.setCourseDescription("Uvod u Javu");
        course.setIsPublished(true);

        assertEquals(1L, course.getCourseId());
        assertEquals("Java Osnove", course.getCourseTitle());
        assertEquals("Uvod u Javu", course.getCourseDescription());
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
}
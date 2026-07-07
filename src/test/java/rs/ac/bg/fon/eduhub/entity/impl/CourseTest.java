package rs.ac.bg.fon.eduhub.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    void testCourseTitleNullIsInvalid() {
        course.setCourseTitle(null);

        assertNull(course.getCourseTitle());
    }

    @Test
    void testCourseTitleBlankIsInvalid() {
        course.setCourseTitle("   ");

        assertTrue(course.getCourseTitle().isBlank());
    }

    @Test
    void testCourseTitleTooLongIsInvalid() {
        String tooLongTitle = "a".repeat(151);
        course.setCourseTitle(tooLongTitle);

        assertTrue(course.getCourseTitle().length() > 150);
    }

    @Test
    void testCourseDescriptionNullIsInvalid() {
        course.setCourseDescription(null);

        assertNull(course.getCourseDescription());
    }

    @Test
    void testCourseDescriptionBlankIsInvalid() {
        course.setCourseDescription("   ");

        assertTrue(course.getCourseDescription().isBlank());
    }
}
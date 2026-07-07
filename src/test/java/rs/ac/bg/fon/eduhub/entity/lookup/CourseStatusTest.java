package rs.ac.bg.fon.eduhub.entity.lookup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit testovi za entitet {@link CourseStatus}.
 *
 * @author Mihajlo Ristanovic
 */
class CourseStatusTest {

    private CourseStatus courseStatus;

    @BeforeEach
    void setUp() {
        courseStatus = new CourseStatus();
    }

    @Test
    void testSetCourseStatusId() {
        courseStatus.setCourseStatusId(1L);

        assertEquals(1L, courseStatus.getCourseStatusId());
    }

    @Test
    void testSetCourseStatusName() {
        courseStatus.setCourseStatusName("DRAFT");

        assertEquals("DRAFT", courseStatus.getCourseStatusName());
    }

    @Test
    void testEqualsSameId() {
        CourseStatus first = new CourseStatus(1L, "DRAFT");
        CourseStatus second = new CourseStatus(1L, "PUBLISHED");

        assertEquals(first, second);
    }

    @Test
    void testEqualsDifferentId() {
        CourseStatus first = new CourseStatus(1L, "DRAFT");
        CourseStatus second = new CourseStatus(2L, "DRAFT");

        assertNotEquals(first, second);
    }

    @Test
    void testHashCodeConsistentWithEquals() {
        CourseStatus first = new CourseStatus(1L, "DRAFT");
        CourseStatus second = new CourseStatus(1L, "DRAFT");

        assertEquals(first.hashCode(), second.hashCode());
    }
}
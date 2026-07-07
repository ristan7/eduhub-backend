package rs.ac.bg.fon.eduhub.entity.lookup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit testovi za entitet {@link CourseLevel}.
 *
 * @author Mihajlo Ristanovic
 */
class CourseLevelTest {

    private CourseLevel courseLevel;

    @BeforeEach
    void setUp() {
        courseLevel = new CourseLevel();
    }

    @Test
    void testSetCourseLevelId() {
        courseLevel.setCourseLevelId(1L);

        assertEquals(1L, courseLevel.getCourseLevelId());
    }

    @Test
    void testSetCourseLevelName() {
        courseLevel.setCourseLevelName("BEGINNER");

        assertEquals("BEGINNER", courseLevel.getCourseLevelName());
    }

    @Test
    void testEqualsSameId() {
        CourseLevel first = new CourseLevel(1L, "BEGINNER");
        CourseLevel second = new CourseLevel(1L, "ADVANCED");

        assertEquals(first, second);
    }

    @Test
    void testEqualsDifferentId() {
        CourseLevel first = new CourseLevel(1L, "BEGINNER");
        CourseLevel second = new CourseLevel(2L, "BEGINNER");

        assertNotEquals(first, second);
    }

    @Test
    void testHashCodeConsistentWithEquals() {
        CourseLevel first = new CourseLevel(1L, "BEGINNER");
        CourseLevel second = new CourseLevel(1L, "BEGINNER");

        assertEquals(first.hashCode(), second.hashCode());
    }
}
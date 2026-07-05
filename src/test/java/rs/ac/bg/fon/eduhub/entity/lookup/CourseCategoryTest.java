package rs.ac.bg.fon.eduhub.entity.lookup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit testovi za entitet {@link CourseCategory}.
 *
 * @author Mihajlo Ristanovic
 */
class CourseCategoryTest {

    private CourseCategory courseCategory;

    @BeforeEach
    void setUp() {
        courseCategory = new CourseCategory();
    }

    @Test
    void testGettersAndSetters() {
        courseCategory.setCourseCategoryId(1L);
        courseCategory.setCourseCategoryName("PROGRAMMING");

        assertEquals(1L, courseCategory.getCourseCategoryId());
        assertEquals("PROGRAMMING", courseCategory.getCourseCategoryName());
    }

    @Test
    void testEqualsSameId() {
        CourseCategory first = new CourseCategory(1L, "PROGRAMMING");
        CourseCategory second = new CourseCategory(1L, "DESIGN");

        assertEquals(first, second);
    }

    @Test
    void testEqualsDifferentId() {
        CourseCategory first = new CourseCategory(1L, "PROGRAMMING");
        CourseCategory second = new CourseCategory(2L, "PROGRAMMING");

        assertNotEquals(first, second);
    }

    @Test
    void testHashCodeConsistentWithEquals() {
        CourseCategory first = new CourseCategory(1L, "PROGRAMMING");
        CourseCategory second = new CourseCategory(1L, "PROGRAMMING");

        assertEquals(first.hashCode(), second.hashCode());
    }
}
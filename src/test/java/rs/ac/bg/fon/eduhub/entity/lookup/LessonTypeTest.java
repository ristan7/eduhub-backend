package rs.ac.bg.fon.eduhub.entity.lookup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit testovi za entitet {@link LessonType}.
 *
 * @author Mihajlo Ristanovic
 */
class LessonTypeTest {

    private LessonType lessonType;

    @BeforeEach
    void setUp() {
        lessonType = new LessonType();
    }

    @Test
    void testGettersAndSetters() {
        lessonType.setLessonTypeId(1L);
        lessonType.setLessonTypeName("VIDEO");

        assertEquals(1L, lessonType.getLessonTypeId());
        assertEquals("VIDEO", lessonType.getLessonTypeName());
    }

    @Test
    void testEqualsSameId() {
        LessonType first = new LessonType(1L, "VIDEO");
        LessonType second = new LessonType(1L, "QUIZ");

        assertEquals(first, second);
    }

    @Test
    void testEqualsDifferentId() {
        LessonType first = new LessonType(1L, "VIDEO");
        LessonType second = new LessonType(2L, "VIDEO");

        assertNotEquals(first, second);
    }

    @Test
    void testHashCodeConsistentWithEquals() {
        LessonType first = new LessonType(1L, "VIDEO");
        LessonType second = new LessonType(1L, "VIDEO");

        assertEquals(first.hashCode(), second.hashCode());
    }
}
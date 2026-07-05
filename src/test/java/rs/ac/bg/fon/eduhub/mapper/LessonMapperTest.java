package rs.ac.bg.fon.eduhub.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rs.ac.bg.fon.eduhub.dto.LessonDto;
import rs.ac.bg.fon.eduhub.entity.impl.Course;
import rs.ac.bg.fon.eduhub.entity.impl.Lesson;
import rs.ac.bg.fon.eduhub.entity.lookup.LessonType;

/**
 * JUnit testovi za {@link LessonMapper}.
 *
 * @author Mihajlo Ristanovic
 */
class LessonMapperTest {

    private LessonMapper lessonMapper;

    @BeforeEach
    void setUp() {
        lessonMapper = new LessonMapper();
    }

    @Test
    void testToDtoMapsAllFields() {
        Course course = new Course();
        course.setCourseId(1L);

        Lesson lesson = new Lesson();
        lesson.setLessonId(1L);
        lesson.setLessonTitle("Uvod");
        lesson.setLessonOrderIndex(1);
        lesson.setIsAvailable(true);
        lesson.setCourse(course);
        lesson.setLessonType(new LessonType(1L, "VIDEO"));

        LessonDto dto = lessonMapper.toDto(lesson);

        assertEquals(1L, dto.lessonId());
        assertEquals("Uvod", dto.lessonTitle());
        assertEquals(1, dto.lessonOrderIndex());
        assertEquals(true, dto.isAvailable());
        assertEquals(1L, dto.courseId());
        assertEquals("VIDEO", dto.lessonTypeName());
    }

    @Test
    void testToDtoWithNullAssociationsReturnsNullFields() {
        Lesson lesson = new Lesson();
        lesson.setLessonId(1L);

        LessonDto dto = lessonMapper.toDto(lesson);

        assertNull(dto.courseId());
        assertNull(dto.lessonTypeId());
    }

    @Test
    void testToDtoWithNullLessonReturnsNull() {
        assertNull(lessonMapper.toDto(null));
    }
}
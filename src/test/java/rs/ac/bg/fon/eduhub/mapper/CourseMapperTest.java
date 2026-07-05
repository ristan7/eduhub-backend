package rs.ac.bg.fon.eduhub.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rs.ac.bg.fon.eduhub.dto.CourseDto;
import rs.ac.bg.fon.eduhub.entity.impl.Course;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseCategory;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseLevel;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseStatus;

/**
 * JUnit testovi za {@link CourseMapper}.
 *
 * @author Mihajlo Ristanovic
 */
class CourseMapperTest {

    private CourseMapper courseMapper;

    @BeforeEach
    void setUp() {
        courseMapper = new CourseMapper();
    }

    @Test
    void testToDtoMapsAllFields() {
        User author = new User();
        author.setUserId(1L);
        author.setFirstName("Ana");
        author.setLastName("Jovanovic");

        Course course = new Course();
        course.setCourseId(1L);
        course.setCourseTitle("Java Osnove");
        course.setCourseDescription("Opis");
        course.setIsPublished(true);
        course.setAuthor(author);
        course.setCourseCategory(new CourseCategory(1L, "PROGRAMMING"));
        course.setCourseLevel(new CourseLevel(1L, "BEGINNER"));
        course.setCourseStatus(new CourseStatus(2L, "PUBLISHED"));

        CourseDto dto = courseMapper.toDto(course);

        assertEquals(1L, dto.courseId());
        assertEquals("Java Osnove", dto.courseTitle());
        assertEquals("Opis", dto.courseDescription());
        assertEquals(true, dto.isPublished());
        assertEquals(1L, dto.authorId());
        assertEquals("Ana Jovanovic", dto.authorFullName());
        assertEquals("PROGRAMMING", dto.courseCategoryName());
        assertEquals("BEGINNER", dto.courseLevelName());
        assertEquals("PUBLISHED", dto.courseStatusName());
    }

    @Test
    void testToDtoWithNullAssociationsReturnsNullFields() {
        Course course = new Course();
        course.setCourseId(1L);

        CourseDto dto = courseMapper.toDto(course);

        assertNull(dto.authorId());
        assertNull(dto.authorFullName());
        assertNull(dto.courseCategoryId());
        assertNull(dto.courseLevelId());
        assertNull(dto.courseStatusId());
    }

    @Test
    void testToDtoWithNullCourseReturnsNull() {
        assertNull(courseMapper.toDto(null));
    }
}
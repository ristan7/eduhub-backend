package rs.ac.bg.fon.eduhub.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rs.ac.bg.fon.eduhub.dto.EnrollmentDto;
import rs.ac.bg.fon.eduhub.entity.impl.Course;
import rs.ac.bg.fon.eduhub.entity.impl.Enrollment;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.EnrollmentStatus;

/**
 * JUnit testovi za {@link EnrollmentMapper}.
 *
 * @author Mihajlo Ristanovic
 */
class EnrollmentMapperTest {

    private EnrollmentMapper enrollmentMapper;

    @BeforeEach
    void setUp() {
        enrollmentMapper = new EnrollmentMapper();
    }

    @Test
    void testToDtoMapsAllFields() {
        User student = new User();
        student.setUserId(1L);
        student.setFirstName("Petar");
        student.setLastName("Nikolic");

        Course course = new Course();
        course.setCourseId(1L);
        course.setCourseTitle("Java Osnove");

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(1L);
        enrollment.setProgressPercentage(50);
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentStatus(new EnrollmentStatus(1L, "ACTIVE"));

        EnrollmentDto dto = enrollmentMapper.toDto(enrollment);

        assertEquals(1L, dto.enrollmentId());
        assertEquals(50, dto.progressPercentage());
        assertEquals(1L, dto.studentId());
        assertEquals("Petar Nikolic", dto.studentFullName());
        assertEquals(1L, dto.courseId());
        assertEquals("Java Osnove", dto.courseTitle());
        assertEquals("ACTIVE", dto.enrollmentStatusName());
    }

    @Test
    void testToDtoWithNullAssociationsReturnsNullFields() {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(1L);

        EnrollmentDto dto = enrollmentMapper.toDto(enrollment);

        assertNull(dto.studentId());
        assertNull(dto.courseId());
        assertNull(dto.enrollmentStatusId());
    }

    @Test
    void testToDtoWithNullEnrollmentReturnsNull() {
        assertNull(enrollmentMapper.toDto(null));
    }
}
package rs.ac.bg.fon.eduhub.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rs.ac.bg.fon.eduhub.dto.ReviewDto;
import rs.ac.bg.fon.eduhub.entity.impl.Course;
import rs.ac.bg.fon.eduhub.entity.impl.Enrollment;
import rs.ac.bg.fon.eduhub.entity.impl.Review;
import rs.ac.bg.fon.eduhub.entity.impl.User;

/**
 * JUnit testovi za {@link ReviewMapper}.
 *
 * @author Mihajlo Ristanovic
 */
class ReviewMapperTest {

    private ReviewMapper reviewMapper;

    @BeforeEach
    void setUp() {
        reviewMapper = new ReviewMapper();
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
        enrollment.setStudent(student);
        enrollment.setCourse(course);

        Review review = new Review();
        review.setReviewId(1L);
        review.setRating(5);
        review.setComment("Odlicno");
        review.setEnrollment(enrollment);

        ReviewDto dto = reviewMapper.toDto(review);

        assertEquals(1L, dto.reviewId());
        assertEquals(5, dto.rating());
        assertEquals("Odlicno", dto.comment());
        assertEquals(1L, dto.enrollmentId());
        assertEquals(1L, dto.courseId());
        assertEquals("Java Osnove", dto.courseTitle());
        assertEquals(1L, dto.studentId());
        assertEquals("Petar Nikolic", dto.studentFullName());
    }

    @Test
    void testToDtoWithNullEnrollmentReturnsNullFields() {
        Review review = new Review();
        review.setReviewId(1L);
        review.setRating(4);

        ReviewDto dto = reviewMapper.toDto(review);

        assertNull(dto.enrollmentId());
        assertNull(dto.courseId());
        assertNull(dto.studentId());
    }

    @Test
    void testToDtoWithNullReviewReturnsNull() {
        assertNull(reviewMapper.toDto(null));
    }
}
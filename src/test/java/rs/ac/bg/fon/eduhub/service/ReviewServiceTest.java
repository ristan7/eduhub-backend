package rs.ac.bg.fon.eduhub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import rs.ac.bg.fon.eduhub.dto.CreateReviewRequest;
import rs.ac.bg.fon.eduhub.dto.ReviewDto;
import rs.ac.bg.fon.eduhub.entity.impl.Enrollment;
import rs.ac.bg.fon.eduhub.entity.impl.Review;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.mapper.ReviewMapper;
import rs.ac.bg.fon.eduhub.repository.CourseRepository;
import rs.ac.bg.fon.eduhub.repository.EnrollmentRepository;
import rs.ac.bg.fon.eduhub.repository.ReviewRepository;
import rs.ac.bg.fon.eduhub.repository.UserRepository;

/**
 * JUnit testovi za servis {@link ReviewService}, uz mokovanje svih
 * zavisnosti (SO19, SO20).
 *
 * @author Mihajlo Ristanovic
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewMapper reviewMapper;

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService(reviewRepository, enrollmentRepository,
                courseRepository, userRepository, reviewMapper);
    }

    @Test
    void testAddReviewSuccess() {
        Long enrollmentId = 1L;
        CreateReviewRequest request = new CreateReviewRequest(5, "Odlican kurs!");
        Authentication authentication = mock(Authentication.class);

        User student = new User();
        student.setUserId(1L);

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(enrollmentId);
        enrollment.setStudent(student);

        ReviewDto expectedDto = new ReviewDto(1L, 5, "Odlican kurs!", null, enrollmentId, 1L, "Java", 1L, "Petar Nikolic");

        when(authentication.getName()).thenReturn("student@eduhub.com");
        when(userRepository.findByUserEmail("student@eduhub.com")).thenReturn(Optional.of(student));
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(reviewRepository.findByEnrollment_EnrollmentId(enrollmentId)).thenReturn(Optional.empty());
        when(reviewMapper.toDto(any(Review.class))).thenReturn(expectedDto);

        ReviewDto result = reviewService.addReview(enrollmentId, request, authentication);

        assertEquals(expectedDto, result);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void testAddReviewForbiddenForNonOwner() {
        Long enrollmentId = 1L;
        CreateReviewRequest request = new CreateReviewRequest(5, "Odlican kurs!");
        Authentication authentication = mock(Authentication.class);

        User owner = new User();
        owner.setUserId(1L);
        User intruder = new User();
        intruder.setUserId(2L);

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(enrollmentId);
        enrollment.setStudent(owner);

        when(authentication.getName()).thenReturn("intruder@eduhub.com");
        when(userRepository.findByUserEmail("intruder@eduhub.com")).thenReturn(Optional.of(intruder));
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        assertThrows(AccessDeniedException.class, () -> reviewService.addReview(enrollmentId, request, authentication));
    }

    @Test
    void testAddReviewAlreadyExists() {
        Long enrollmentId = 1L;
        CreateReviewRequest request = new CreateReviewRequest(5, "Odlican kurs!");
        Authentication authentication = mock(Authentication.class);

        User student = new User();
        student.setUserId(1L);

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(enrollmentId);
        enrollment.setStudent(student);

        when(authentication.getName()).thenReturn("student@eduhub.com");
        when(userRepository.findByUserEmail("student@eduhub.com")).thenReturn(Optional.of(student));
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(reviewRepository.findByEnrollment_EnrollmentId(enrollmentId)).thenReturn(Optional.of(new Review()));

        assertThrows(IllegalArgumentException.class, () -> reviewService.addReview(enrollmentId, request, authentication));
    }

    @Test
    void testGetReviewsByCourseSuccess() {
        Long courseId = 1L;
        Review review = new Review();
        ReviewDto dto = new ReviewDto(1L, 5, "Odlican kurs!", null, 1L, courseId, "Java", 1L, "Petar Nikolic");

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(new rs.ac.bg.fon.eduhub.entity.impl.Course()));
        when(reviewRepository.findByCourseId(courseId)).thenReturn(List.of(review));
        when(reviewMapper.toDto(review)).thenReturn(dto);

        List<ReviewDto> result = reviewService.getReviewsByCourse(courseId);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void testGetReviewsByCourseNotFound() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> reviewService.getReviewsByCourse(99L));
    }
}
package rs.ac.bg.fon.eduhub.service;

import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
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

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    public ReviewService(ReviewRepository reviewRepository,
                         EnrollmentRepository enrollmentRepository,
                         CourseRepository courseRepository,
                         UserRepository userRepository,
                         ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.reviewMapper = reviewMapper;
    }

    // SO19 - Ocenjivanje kursa
    public ReviewDto addReview(Long enrollmentId, CreateReviewRequest request, Authentication authentication) {
        User student = currentUser(authentication);
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found: " + enrollmentId));

        if (enrollment.getStudent() == null || !enrollment.getStudent().getUserId().equals(student.getUserId())) {
            throw new AccessDeniedException("You can only review your own enrollments.");
        }

        reviewRepository.findByEnrollment_EnrollmentId(enrollmentId).ifPresent(r -> {
            throw new IllegalArgumentException("This enrollment already has a review.");
        });

        Review review = new Review();
        review.setRating(request.rating());
        review.setComment(request.comment());
        review.setEnrollment(enrollment);

        reviewRepository.save(review);
        return reviewMapper.toDto(review);
    }

    // SO20 - Pregled ocena i komentara za kurs
    public List<ReviewDto> getReviewsByCourse(Long courseId) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        return reviewRepository.findByCourseId(courseId).stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    private User currentUser(Authentication authentication) {
        return userRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));
    }
}
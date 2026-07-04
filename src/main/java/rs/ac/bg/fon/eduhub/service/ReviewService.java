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

/**
 * Servis koji implementira poslovnu logiku ocenjivanja kurseva i pregleda
 * ocena (SO19, SO20). Ocenjivanje je dozvoljeno samo studentu čija je
 * prijava u pitanju, i to najviše jednom po prijavi.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    /**
     * Kreira servis sa svim potrebnim zavisnostima.
     *
     * @param reviewRepository repozitorijum ocena
     * @param enrollmentRepository repozitorijum prijava
     * @param courseRepository repozitorijum kurseva
     * @param userRepository repozitorijum korisnika
     * @param reviewMapper mapper za konverziju entiteta ocene u DTO
     */
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

    /**
     * Dodaje ocenu i komentar za kurs, vezano za konkretnu prijavu (SO19).
     * Dozvoljeno samo studentu kome prijava pripada, i samo jednom po
     * prijavi.
     *
     * @param enrollmentId identifikator prijave koja se ocenjuje
     * @param request ocena i komentar
     * @param authentication autentifikacija trenutno prijavljenog korisnika (student)
     * @return DTO novokreirane ocene
     * @throws IllegalArgumentException ako prijava sa datim identifikatorom ne postoji, ili već ima ocenu
     * @throws AccessDeniedException ako prijava ne pripada trenutno prijavljenom korisniku
     */
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

    /**
     * Vraća listu svih ocena ostavljenih za zadati kurs (SO20).
     *
     * @param courseId identifikator kursa
     * @return lista ocena kursa
     * @throws IllegalArgumentException ako kurs sa datim identifikatorom ne postoji
     */
    public List<ReviewDto> getReviewsByCourse(Long courseId) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        return reviewRepository.findByCourseId(courseId).stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    /**
     * Pronalazi entitet korisnika na osnovu email adrese iz autentifikacije.
     *
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return pronađeni entitet korisnika
     * @throws IllegalStateException ako autentifikovani korisnik neočekivano ne postoji u bazi
     */
    private User currentUser(Authentication authentication) {
        return userRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));
    }
}
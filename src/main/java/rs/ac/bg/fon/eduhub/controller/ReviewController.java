package rs.ac.bg.fon.eduhub.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.bg.fon.eduhub.dto.CreateReviewRequest;
import rs.ac.bg.fon.eduhub.dto.ReviewDto;
import rs.ac.bg.fon.eduhub.service.ReviewService;

/**
 * REST kontroler za ocenjivanje kurseva i pregled ocena (SO19, SO20).
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Kreira kontroler sa injektovanim servisom za ocene.
     *
     * @param reviewService servis koji implementira poslovnu logiku ocenjivanja
     */
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Dodaje ocenu i komentar za kurs, vezano za konkretnu prijavu
     * (SO19).
     *
     * @param enrollmentId identifikator prijave koja se ocenjuje
     * @param request ocena i komentar
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return HTTP 201 sa podacima novokreirane ocene
     */
    @PostMapping("/api/enrollments/{enrollmentId}/review")
    public ResponseEntity<ReviewDto> addReview(@PathVariable Long enrollmentId,
                                               @Valid @RequestBody CreateReviewRequest request,
                                               Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.addReview(enrollmentId, request, authentication));
    }

    /**
     * Vraća listu svih ocena ostavljenih za zadati kurs (SO20).
     *
     * @param courseId identifikator kursa
     * @return HTTP 200 sa listom ocena kursa
     */
    @GetMapping("/api/courses/{courseId}/reviews")
    public ResponseEntity<List<ReviewDto>> getReviewsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(reviewService.getReviewsByCourse(courseId));
    }
}
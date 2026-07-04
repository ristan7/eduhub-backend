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

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // SO19 - Ocenjivanje kursa
    @PostMapping("/api/enrollments/{enrollmentId}/review")
    public ResponseEntity<ReviewDto> addReview(@PathVariable Long enrollmentId,
                                               @Valid @RequestBody CreateReviewRequest request,
                                               Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.addReview(enrollmentId, request, authentication));
    }

    // SO20 - Pregled ocena i komentara za kurs
    @GetMapping("/api/courses/{courseId}/reviews")
    public ResponseEntity<List<ReviewDto>> getReviewsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(reviewService.getReviewsByCourse(courseId));
    }
}
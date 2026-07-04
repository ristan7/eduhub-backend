package rs.ac.bg.fon.eduhub.mapper;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.eduhub.dto.ReviewDto;
import rs.ac.bg.fon.eduhub.entity.impl.Review;

@Component
public class ReviewMapper {

    public ReviewDto toDto(Review review) {
        if (review == null) {
            return null;
        }
        var enrollment = review.getEnrollment();
        return new ReviewDto(
                review.getReviewId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                enrollment != null ? enrollment.getEnrollmentId() : null,
                enrollment != null && enrollment.getCourse() != null ? enrollment.getCourse().getCourseId() : null,
                enrollment != null && enrollment.getCourse() != null ? enrollment.getCourse().getCourseTitle() : null,
                enrollment != null && enrollment.getStudent() != null ? enrollment.getStudent().getUserId() : null,
                enrollment != null && enrollment.getStudent() != null
                        ? enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName()
                        : null
        );
    }
}
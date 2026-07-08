package rs.ac.bg.fon.eduhub.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit testovi za entitet {@link Review}.
 *
 * @author Mihajlo Ristanovic
 */
class ReviewTest {

    private static Validator validator;

    private Review review;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        review = validReview();
    }

    private Review validReview() {
        Review r = new Review();
        r.setRating(5);
        return r;
    }

    private boolean hasViolationFor(Set<ConstraintViolation<Review>> violations, String propertyName) {
        return violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals(propertyName));
    }

    @Test
    void testSetReviewId() {
        review.setReviewId(1L);

        assertEquals(1L, review.getReviewId());
    }

    @Test
    void testSetRating() {
        review.setRating(5);

        assertEquals(5, review.getRating());
    }

    @Test
    void testSetComment() {
        review.setComment("Odlican kurs!");

        assertEquals("Odlican kurs!", review.getComment());
    }

    @Test
    void testEqualsSameId() {
        Review first = new Review();
        first.setReviewId(1L);

        Review second = new Review();
        second.setReviewId(1L);

        assertEquals(first, second);
    }

    @Test
    void testEqualsDifferentId() {
        Review first = new Review();
        first.setReviewId(1L);

        Review second = new Review();
        second.setReviewId(2L);

        assertNotEquals(first, second);
    }

    @Test
    void testOnCreateSetsCreatedAt() {
        assertEquals(null, review.getCreatedAt());
        review.onCreate();
        assertNotNull(review.getCreatedAt());
    }

    @Test
    void testRatingValidPassesValidation() {
        review.setRating(3);

        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        assertFalse(hasViolationFor(violations, "rating"));
    }

    @Test
    void testRatingNullFailsValidation() {
        review.setRating(null);

        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        assertTrue(hasViolationFor(violations, "rating"));
    }

    @Test
    void testRatingBelowMinimumFailsValidation() {
        review.setRating(0);

        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        assertTrue(hasViolationFor(violations, "rating"));
    }

    @Test
    void testRatingAtMinimumPassesValidation() {
        review.setRating(1);

        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        assertFalse(hasViolationFor(violations, "rating"));
    }

    @Test
    void testRatingAtMaximumPassesValidation() {
        review.setRating(5);

        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        assertFalse(hasViolationFor(violations, "rating"));
    }

    @Test
    void testRatingAboveMaximumFailsValidation() {
        review.setRating(6);

        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        assertTrue(hasViolationFor(violations, "rating"));
    }
}
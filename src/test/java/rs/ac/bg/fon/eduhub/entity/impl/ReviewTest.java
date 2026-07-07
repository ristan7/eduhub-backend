package rs.ac.bg.fon.eduhub.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit testovi za entitet {@link Review}.
 *
 * @author Mihajlo Ristanovic
 */
class ReviewTest {

    private Review review;

    @BeforeEach
    void setUp() {
        review = new Review();
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
    void testRatingNullIsInvalid() {
        review.setRating(null);

        assertNull(review.getRating());
    }

    @Test
    void testRatingBelowMinimumIsInvalid() {
        review.setRating(0);

        assertTrue(review.getRating() < 1);
    }

    @Test
    void testRatingAboveMaximumIsInvalid() {
        review.setRating(6);

        assertTrue(review.getRating() > 5);
    }
}
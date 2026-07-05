package rs.ac.bg.fon.eduhub.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import rs.ac.bg.fon.eduhub.entity.impl.Course;
import rs.ac.bg.fon.eduhub.entity.impl.Enrollment;
import rs.ac.bg.fon.eduhub.entity.impl.Review;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseCategory;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseLevel;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseStatus;
import rs.ac.bg.fon.eduhub.entity.lookup.EnrollmentStatus;
import rs.ac.bg.fon.eduhub.entity.lookup.Role;

/**
 * {@code @DataJpaTest} integracioni testovi za {@link ReviewRepository}.
 *
 * @author Mihajlo Ristanovic
 */
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
class ReviewRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReviewRepository reviewRepository;

    private Enrollment enrollment;
    private Course course;

    @BeforeEach
    void setUp() {
        Role studentRole = new Role();
        studentRole.setRoleName("STUDENT");
        entityManager.persist(studentRole);

        Role instructorRole = new Role();
        instructorRole.setRoleName("INSTRUCTOR");
        entityManager.persist(instructorRole);

        User student = new User();
        student.setUserEmail("student@eduhub.com");
        student.setPassword("hashed");
        student.setFirstName("Petar");
        student.setLastName("Nikolic");
        student.setIsActive(true);
        student.setRole(studentRole);
        entityManager.persist(student);

        User author = new User();
        author.setUserEmail("author@eduhub.com");
        author.setPassword("hashed");
        author.setFirstName("Ana");
        author.setLastName("Jovanovic");
        author.setIsActive(true);
        author.setRole(instructorRole);
        entityManager.persist(author);

        CourseCategory category = new CourseCategory();
        category.setCourseCategoryName("PROGRAMMING");
        entityManager.persist(category);

        CourseLevel level = new CourseLevel();
        level.setCourseLevelName("BEGINNER");
        entityManager.persist(level);

        CourseStatus status = new CourseStatus();
        status.setCourseStatusName("DRAFT");
        entityManager.persist(status);

        course = new Course();
        course.setCourseTitle("Java Osnove");
        course.setCourseDescription("Opis");
        course.setIsPublished(false);
        course.setAuthor(author);
        course.setCourseCategory(category);
        course.setCourseLevel(level);
        course.setCourseStatus(status);
        entityManager.persist(course);

        EnrollmentStatus activeStatus = new EnrollmentStatus();
        activeStatus.setEnrollmentStatusName("ACTIVE");
        entityManager.persist(activeStatus);

        enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentStatus(activeStatus);
        enrollment.setProgressPercentage(0);
        entityManager.persist(enrollment);
    }

    @Test
    void testFindByEnrollmentIdFound() {
        persistReview(5, "Odlicno");

        Optional<Review> result = reviewRepository.findByEnrollment_EnrollmentId(enrollment.getEnrollmentId());

        assertTrue(result.isPresent());
        assertEquals(5, result.get().getRating());
    }

    @Test
    void testFindByEnrollmentIdNotFound() {
        Optional<Review> result = reviewRepository.findByEnrollment_EnrollmentId(999L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByCourseId() {
        persistReview(4, "Dobar kurs");

        List<Review> result = reviewRepository.findByCourseId(course.getCourseId());

        assertEquals(1, result.size());
        assertEquals(4, result.get(0).getRating());
    }

    private void persistReview(int rating, String comment) {
        Review review = new Review();
        review.setRating(rating);
        review.setComment(comment);
        review.setEnrollment(enrollment);
        entityManager.persistAndFlush(review);
    }
}
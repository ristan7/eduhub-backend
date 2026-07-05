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
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseCategory;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseLevel;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseStatus;
import rs.ac.bg.fon.eduhub.entity.lookup.EnrollmentStatus;
import rs.ac.bg.fon.eduhub.entity.lookup.Role;

/**
 * {@code @DataJpaTest} integracioni testovi za {@link EnrollmentRepository}.
 *
 * @author Mihajlo Ristanovic
 */
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
class EnrollmentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private User student;
    private Course course;
    private EnrollmentStatus activeStatus;

    @BeforeEach
    void setUp() {
        Role studentRole = new Role();
        studentRole.setRoleName("STUDENT");
        entityManager.persist(studentRole);

        Role instructorRole = new Role();
        instructorRole.setRoleName("INSTRUCTOR");
        entityManager.persist(instructorRole);

        student = new User();
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

        activeStatus = new EnrollmentStatus();
        activeStatus.setEnrollmentStatusName("ACTIVE");
        entityManager.persist(activeStatus);
    }

    @Test
    void testFindByStudentId() {
        persistEnrollment();

        List<Enrollment> result = enrollmentRepository.findByStudent_UserId(student.getUserId());

        assertEquals(1, result.size());
        assertEquals(course.getCourseId(), result.get(0).getCourse().getCourseId());
    }

    @Test
    void testFindByStudentIdAndCourseIdFound() {
        persistEnrollment();

        Optional<Enrollment> result = enrollmentRepository
                .findByStudent_UserIdAndCourse_CourseId(student.getUserId(), course.getCourseId());

        assertTrue(result.isPresent());
    }

    @Test
    void testFindByStudentIdAndCourseIdNotFound() {
        Optional<Enrollment> result = enrollmentRepository
                .findByStudent_UserIdAndCourse_CourseId(student.getUserId(), 999L);

        assertTrue(result.isEmpty());
    }

    private void persistEnrollment() {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentStatus(activeStatus);
        enrollment.setProgressPercentage(0);
        entityManager.persistAndFlush(enrollment);
    }
}
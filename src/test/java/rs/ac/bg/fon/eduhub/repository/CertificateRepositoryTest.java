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
import rs.ac.bg.fon.eduhub.entity.impl.Certificate;
import rs.ac.bg.fon.eduhub.entity.impl.Course;
import rs.ac.bg.fon.eduhub.entity.impl.Enrollment;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseCategory;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseLevel;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseStatus;
import rs.ac.bg.fon.eduhub.entity.lookup.EnrollmentStatus;
import rs.ac.bg.fon.eduhub.entity.lookup.Role;

/**
 * {@code @DataJpaTest} integracioni testovi za {@link CertificateRepository}.
 *
 * @author Mihajlo Ristanovic
 */
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
class CertificateRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CertificateRepository certificateRepository;

    private User student;
    private Enrollment enrollment;

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

        Course course = new Course();
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
        persistCertificate("CERT-ABCD1234-2026");

        Optional<Certificate> result = certificateRepository.findByEnrollment_EnrollmentId(enrollment.getEnrollmentId());

        assertTrue(result.isPresent());
        assertEquals("CERT-ABCD1234-2026", result.get().getCode());
    }

    @Test
    void testFindByEnrollmentIdNotFound() {
        Optional<Certificate> result = certificateRepository.findByEnrollment_EnrollmentId(999L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByStudentId() {
        persistCertificate("CERT-ABCD1234-2026");

        List<Certificate> result = certificateRepository.findByEnrollment_Student_UserId(student.getUserId());

        assertEquals(1, result.size());
    }

    private void persistCertificate(String code) {
        Certificate certificate = new Certificate();
        certificate.setCode(code);
        certificate.setEnrollment(enrollment);
        entityManager.persistAndFlush(certificate);
    }
}
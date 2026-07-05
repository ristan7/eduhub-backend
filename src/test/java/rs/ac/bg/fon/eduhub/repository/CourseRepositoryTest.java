package rs.ac.bg.fon.eduhub.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import rs.ac.bg.fon.eduhub.entity.impl.Course;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseCategory;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseLevel;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseStatus;
import rs.ac.bg.fon.eduhub.entity.lookup.Role;

/**
 * {@code @DataJpaTest} integracioni testovi za {@link CourseRepository}.
 *
 * @author Mihajlo Ristanovic
 */
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
class CourseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseRepository courseRepository;

    private CourseCategory programming;
    private CourseCategory design;
    private CourseLevel beginner;
    private CourseStatus draft;
    private User author;

    @BeforeEach
    void setUp() {
        programming = new CourseCategory();
        programming.setCourseCategoryName("PROGRAMMING");
        entityManager.persist(programming);

        design = new CourseCategory();
        design.setCourseCategoryName("DESIGN");
        entityManager.persist(design);

        beginner = new CourseLevel();
        beginner.setCourseLevelName("BEGINNER");
        entityManager.persist(beginner);

        draft = new CourseStatus();
        draft.setCourseStatusName("DRAFT");
        entityManager.persist(draft);

        Role role = new Role();
        role.setRoleName("INSTRUCTOR");
        entityManager.persist(role);

        author = new User();
        author.setUserEmail("author@eduhub.com");
        author.setPassword("hashed");
        author.setFirstName("Ana");
        author.setLastName("Jovanovic");
        author.setIsActive(true);
        author.setRole(role);
        entityManager.persist(author);
    }

    @Test
    void testSearchByKeyword() {
        persistCourse("Java Osnove", programming);
        persistCourse("Dizajn interfejsa", design);

        List<Course> result = courseRepository.search("java", null, null);

        assertEquals(1, result.size());
        assertEquals("Java Osnove", result.get(0).getCourseTitle());
    }

    @Test
    void testSearchByCategory() {
        persistCourse("Java Osnove", programming);
        persistCourse("Dizajn interfejsa", design);

        List<Course> result = courseRepository.search(null, design.getCourseCategoryId(), null);

        assertEquals(1, result.size());
        assertEquals("Dizajn interfejsa", result.get(0).getCourseTitle());
    }

    @Test
    void testSearchWithNoFiltersReturnsAll() {
        persistCourse("Java Osnove", programming);
        persistCourse("Dizajn interfejsa", design);

        assertEquals(2, courseRepository.search(null, null, null).size());
    }

    @Test
    void testCountByCourseStatusName() {
        persistCourse("Java Osnove", programming);
        persistCourse("Dizajn interfejsa", design);

        assertEquals(2, courseRepository.countByCourseStatus_CourseStatusName("DRAFT"));
        assertEquals(0, courseRepository.countByCourseStatus_CourseStatusName("PUBLISHED"));
    }

    private void persistCourse(String title, CourseCategory category) {
        Course course = new Course();
        course.setCourseTitle(title);
        course.setCourseDescription("Opis");
        course.setIsPublished(false);
        course.setAuthor(author);
        course.setCourseCategory(category);
        course.setCourseLevel(beginner);
        course.setCourseStatus(draft);
        entityManager.persistAndFlush(course);
    }
}
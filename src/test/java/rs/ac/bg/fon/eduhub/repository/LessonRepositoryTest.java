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
import rs.ac.bg.fon.eduhub.entity.impl.Lesson;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseCategory;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseLevel;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseStatus;
import rs.ac.bg.fon.eduhub.entity.lookup.LessonType;
import rs.ac.bg.fon.eduhub.entity.lookup.Role;

/**
 * {@code @DataJpaTest} integracioni testovi za {@link LessonRepository}.
 *
 * @author Mihajlo Ristanovic
 */
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
class LessonRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LessonRepository lessonRepository;

    private Course course;
    private LessonType videoType;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName("INSTRUCTOR");
        entityManager.persist(role);

        User author = new User();
        author.setUserEmail("author@eduhub.com");
        author.setPassword("hashed");
        author.setFirstName("Ana");
        author.setLastName("Jovanovic");
        author.setIsActive(true);
        author.setRole(role);
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

        videoType = new LessonType();
        videoType.setLessonTypeName("VIDEO");
        entityManager.persist(videoType);
    }

    @Test
    void testFindByCourseIdOrderedByIndex() {
        persistLesson("Druga lekcija", 2);
        persistLesson("Prva lekcija", 1);

        List<Lesson> result = lessonRepository.findByCourse_CourseIdOrderByLessonOrderIndexAsc(course.getCourseId());

        assertEquals(2, result.size());
        assertEquals("Prva lekcija", result.get(0).getLessonTitle());
        assertEquals("Druga lekcija", result.get(1).getLessonTitle());
    }

    @Test
    void testFindByCourseIdReturnsEmptyForUnknownCourse() {
        List<Lesson> result = lessonRepository.findByCourse_CourseIdOrderByLessonOrderIndexAsc(999L);
        assertEquals(0, result.size());
    }

    private void persistLesson(String title, int orderIndex) {
        Lesson lesson = new Lesson();
        lesson.setLessonTitle(title);
        lesson.setLessonOrderIndex(orderIndex);
        lesson.setIsAvailable(true);
        lesson.setCourse(course);
        lesson.setLessonType(videoType);
        entityManager.persistAndFlush(lesson);
    }
}
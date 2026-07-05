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
import rs.ac.bg.fon.eduhub.entity.impl.Material;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseCategory;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseLevel;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseStatus;
import rs.ac.bg.fon.eduhub.entity.lookup.LessonType;
import rs.ac.bg.fon.eduhub.entity.lookup.MaterialType;
import rs.ac.bg.fon.eduhub.entity.lookup.Role;

/**
 * {@code @DataJpaTest} integracioni testovi za {@link MaterialRepository}.
 *
 * @author Mihajlo Ristanovic
 */
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
class MaterialRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MaterialRepository materialRepository;

    private Lesson lesson;
    private MaterialType pdfType;

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

        Course course = new Course();
        course.setCourseTitle("Java Osnove");
        course.setCourseDescription("Opis");
        course.setIsPublished(false);
        course.setAuthor(author);
        course.setCourseCategory(category);
        course.setCourseLevel(level);
        course.setCourseStatus(status);
        entityManager.persist(course);

        LessonType videoType = new LessonType();
        videoType.setLessonTypeName("VIDEO");
        entityManager.persist(videoType);

        lesson = new Lesson();
        lesson.setLessonTitle("Uvod");
        lesson.setLessonOrderIndex(1);
        lesson.setIsAvailable(true);
        lesson.setCourse(course);
        lesson.setLessonType(videoType);
        entityManager.persist(lesson);

        pdfType = new MaterialType();
        pdfType.setMaterialTypeName("PDF");
        entityManager.persist(pdfType);
    }

    @Test
    void testFindByLessonIdOrderedByIndex() {
        persistMaterial("Drugi materijal", 2);
        persistMaterial("Prvi materijal", 1);

        List<Material> result = materialRepository.findByLesson_LessonIdOrderByMaterialOrderIndexAsc(lesson.getLessonId());

        assertEquals(2, result.size());
        assertEquals("Prvi materijal", result.get(0).getMaterialName());
        assertEquals("Drugi materijal", result.get(1).getMaterialName());
    }

    @Test
    void testFindByLessonIdReturnsEmptyForUnknownLesson() {
        List<Material> result = materialRepository.findByLesson_LessonIdOrderByMaterialOrderIndexAsc(999L);
        assertEquals(0, result.size());
    }

    private void persistMaterial(String name, int orderIndex) {
        Material material = new Material();
        material.setMaterialName(name);
        material.setMaterialOrderIndex(orderIndex);
        material.setLesson(lesson);
        material.setMaterialType(pdfType);
        entityManager.persistAndFlush(material);
    }
}
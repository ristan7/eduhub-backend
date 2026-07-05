package rs.ac.bg.fon.eduhub.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseLevel;

/**
 * {@code @DataJpaTest} integracioni testovi za {@link CourseLevelRepository}.
 *
 * @author Mihajlo Ristanovic
 */
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
class CourseLevelRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseLevelRepository courseLevelRepository;

    @Test
    void testSaveAndFindById() {
        CourseLevel level = new CourseLevel();
        level.setCourseLevelName("BEGINNER");
        CourseLevel saved = entityManager.persistFlushFind(level);

        Optional<CourseLevel> result = courseLevelRepository.findById(saved.getCourseLevelId());

        assertTrue(result.isPresent());
        assertEquals("BEGINNER", result.get().getCourseLevelName());
    }
}
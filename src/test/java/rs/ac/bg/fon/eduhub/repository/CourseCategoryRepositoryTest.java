package rs.ac.bg.fon.eduhub.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseCategory;

/**
 * {@code @DataJpaTest} integracioni testovi za {@link CourseCategoryRepository}.
 *
 * @author Mihajlo Ristanovic
 */
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
class CourseCategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseCategoryRepository courseCategoryRepository;

    @Test
    void testSaveAndFindById() {
        CourseCategory category = new CourseCategory();
        category.setCourseCategoryName("PROGRAMMING");
        CourseCategory saved = entityManager.persistFlushFind(category);

        Optional<CourseCategory> result = courseCategoryRepository.findById(saved.getCourseCategoryId());

        assertTrue(result.isPresent());
        assertEquals("PROGRAMMING", result.get().getCourseCategoryName());
    }
}
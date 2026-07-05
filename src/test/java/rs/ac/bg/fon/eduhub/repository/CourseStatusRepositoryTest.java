package rs.ac.bg.fon.eduhub.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseStatus;

/**
 * {@code @DataJpaTest} integracioni testovi za {@link CourseStatusRepository}.
 *
 * @author Mihajlo Ristanovic
 */
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
class CourseStatusRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseStatusRepository courseStatusRepository;

    @Test
    void testFindByCourseStatusNameFound() {
        CourseStatus status = new CourseStatus();
        status.setCourseStatusName("DRAFT");
        entityManager.persistAndFlush(status);

        Optional<CourseStatus> result = courseStatusRepository.findByCourseStatusName("DRAFT");

        assertTrue(result.isPresent());
        assertEquals("DRAFT", result.get().getCourseStatusName());
    }

    @Test
    void testFindByCourseStatusNameNotFound() {
        Optional<CourseStatus> result = courseStatusRepository.findByCourseStatusName("NEPOSTOJECI");
        assertTrue(result.isEmpty());
    }
}
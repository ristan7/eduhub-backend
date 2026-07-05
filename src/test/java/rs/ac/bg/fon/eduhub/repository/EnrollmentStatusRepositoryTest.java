package rs.ac.bg.fon.eduhub.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import rs.ac.bg.fon.eduhub.entity.lookup.EnrollmentStatus;

/**
 * {@code @DataJpaTest} integracioni testovi za {@link EnrollmentStatusRepository}.
 *
 * @author Mihajlo Ristanovic
 */
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
class EnrollmentStatusRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EnrollmentStatusRepository enrollmentStatusRepository;

    @Test
    void testFindByEnrollmentStatusNameFound() {
        EnrollmentStatus status = new EnrollmentStatus();
        status.setEnrollmentStatusName("ACTIVE");
        entityManager.persistAndFlush(status);

        Optional<EnrollmentStatus> result = enrollmentStatusRepository.findByEnrollmentStatusName("ACTIVE");

        assertTrue(result.isPresent());
        assertEquals("ACTIVE", result.get().getEnrollmentStatusName());
    }

    @Test
    void testFindByEnrollmentStatusNameNotFound() {
        Optional<EnrollmentStatus> result = enrollmentStatusRepository.findByEnrollmentStatusName("NEPOSTOJECI");
        assertTrue(result.isEmpty());
    }
}
package rs.ac.bg.fon.eduhub.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import rs.ac.bg.fon.eduhub.entity.lookup.NotificationType;

/**
 * {@code @DataJpaTest} integracioni testovi za {@link NotificationTypeRepository}.
 *
 * @author Mihajlo Ristanovic
 */
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
class NotificationTypeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationTypeRepository notificationTypeRepository;

    @Test
    void testSaveAndFindById() {
        NotificationType notificationType = new NotificationType();
        notificationType.setNotificationTypeName("SYSTEM");
        NotificationType saved = entityManager.persistFlushFind(notificationType);

        Optional<NotificationType> result = notificationTypeRepository.findById(saved.getNotificationTypeId());

        assertTrue(result.isPresent());
        assertEquals("SYSTEM", result.get().getNotificationTypeName());
    }
}
package rs.ac.bg.fon.eduhub.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import rs.ac.bg.fon.eduhub.entity.impl.Notification;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.NotificationType;
import rs.ac.bg.fon.eduhub.entity.lookup.Role;

/**
 * {@code @DataJpaTest} integracioni testovi za {@link NotificationRepository}.
 *
 * @author Mihajlo Ristanovic
 */
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
class NotificationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationRepository notificationRepository;

    private User user;
    private NotificationType systemType;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName("STUDENT");
        entityManager.persist(role);

        user = new User();
        user.setUserEmail("student@eduhub.com");
        user.setPassword("hashed");
        user.setFirstName("Petar");
        user.setLastName("Nikolic");
        user.setIsActive(true);
        user.setRole(role);
        entityManager.persist(user);

        systemType = new NotificationType();
        systemType.setNotificationTypeName("SYSTEM");
        entityManager.persist(systemType);
    }

    @Test
    void testFindByUserIdOrderedBySentAtDesc() throws InterruptedException {
        persistNotification("Prva notifikacija");
        Thread.sleep(10);
        persistNotification("Druga notifikacija");

        List<Notification> result = notificationRepository.findByUser_UserIdOrderBySentAtDesc(user.getUserId());

        assertEquals(2, result.size());
        assertEquals("Druga notifikacija", result.get(0).getNotificationTitle());
        assertEquals("Prva notifikacija", result.get(1).getNotificationTitle());
    }

    @Test
    void testFindByUserIdReturnsEmptyForUnknownUser() {
        List<Notification> result = notificationRepository.findByUser_UserIdOrderBySentAtDesc(999L);
        assertEquals(0, result.size());
    }

    private void persistNotification(String title) {
        Notification notification = new Notification();
        notification.setNotificationTitle(title);
        notification.setMessage("Poruka");
        notification.setIsRead(false);
        notification.setUser(user);
        notification.setNotificationType(systemType);
        entityManager.persistAndFlush(notification);
    }
}
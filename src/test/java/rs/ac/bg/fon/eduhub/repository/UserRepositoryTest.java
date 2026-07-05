package rs.ac.bg.fon.eduhub.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.Role;

/**
 * {@code @DataJpaTest} integracioni testovi za {@link UserRepository}, koji
 * proveravaju ponašanje custom upita nad stvarnom (H2 in-memory) bazom.
 *
 * @author Mihajlo Ristanovic
 */
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUserEmailFetchesRole() {
        Role role = new Role();
        role.setRoleName("STUDENT");
        entityManager.persist(role);

        User user = buildUser("student@eduhub.com", role);
        entityManager.persistAndFlush(user);
        entityManager.clear();

        Optional<User> result = userRepository.findByUserEmail("student@eduhub.com");

        assertTrue(result.isPresent());
        assertEquals("STUDENT", result.get().getRole().getRoleName());
    }

    @Test
    void testFindByUserEmailNotFound() {
        Optional<User> result = userRepository.findByUserEmail("nepostojeci@eduhub.com");
        assertTrue(result.isEmpty());
    }

    @Test
    void testExistsByUserEmail() {
        Role role = new Role();
        role.setRoleName("STUDENT");
        entityManager.persist(role);
        entityManager.persistAndFlush(buildUser("student@eduhub.com", role));

        assertTrue(userRepository.existsByUserEmail("student@eduhub.com"));
        assertFalse(userRepository.existsByUserEmail("drugi@eduhub.com"));
    }

    @Test
    void testCountByRoleName() {
        Role studentRole = new Role();
        studentRole.setRoleName("STUDENT");
        entityManager.persist(studentRole);

        Role instructorRole = new Role();
        instructorRole.setRoleName("INSTRUCTOR");
        entityManager.persist(instructorRole);

        entityManager.persistAndFlush(buildUser("s1@eduhub.com", studentRole));
        entityManager.persistAndFlush(buildUser("s2@eduhub.com", studentRole));
        entityManager.persistAndFlush(buildUser("i1@eduhub.com", instructorRole));

        assertEquals(2, userRepository.countByRole_RoleName("STUDENT"));
        assertEquals(1, userRepository.countByRole_RoleName("INSTRUCTOR"));
    }

    private User buildUser(String email, Role role) {
        User user = new User();
        user.setUserEmail(email);
        user.setPassword("hashed");
        user.setFirstName("Ime");
        user.setLastName("Prezime");
        user.setIsActive(true);
        user.setRole(role);
        return user;
    }
}
package rs.ac.bg.fon.eduhub.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import rs.ac.bg.fon.eduhub.entity.lookup.Role;

/**
 * {@code @DataJpaTest} integracioni testovi za {@link RoleRepository}.
 *
 * @author Mihajlo Ristanovic
 */
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
class RoleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testFindByRoleNameFound() {
        Role role = new Role();
        role.setRoleName("STUDENT");
        entityManager.persistAndFlush(role);

        Optional<Role> result = roleRepository.findByRoleName("STUDENT");

        assertTrue(result.isPresent());
        assertEquals("STUDENT", result.get().getRoleName());
    }

    @Test
    void testFindByRoleNameNotFound() {
        Optional<Role> result = roleRepository.findByRoleName("NEPOSTOJECA");
        assertTrue(result.isEmpty());
    }
}
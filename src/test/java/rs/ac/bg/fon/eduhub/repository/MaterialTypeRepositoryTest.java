package rs.ac.bg.fon.eduhub.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import rs.ac.bg.fon.eduhub.entity.lookup.MaterialType;

/**
 * {@code @DataJpaTest} integracioni testovi za {@link MaterialTypeRepository}.
 *
 * @author Mihajlo Ristanovic
 */
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
class MaterialTypeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MaterialTypeRepository materialTypeRepository;

    @Test
    void testSaveAndFindById() {
        MaterialType materialType = new MaterialType();
        materialType.setMaterialTypeName("PDF");
        MaterialType saved = entityManager.persistFlushFind(materialType);

        Optional<MaterialType> result = materialTypeRepository.findById(saved.getMaterialTypeId());

        assertTrue(result.isPresent());
        assertEquals("PDF", result.get().getMaterialTypeName());
    }
}
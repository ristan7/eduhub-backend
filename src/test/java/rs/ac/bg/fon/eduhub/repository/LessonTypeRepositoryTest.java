package rs.ac.bg.fon.eduhub.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import rs.ac.bg.fon.eduhub.entity.lookup.LessonType;

/**
 * {@code @DataJpaTest} integracioni testovi za {@link LessonTypeRepository}.
 *
 * @author Mihajlo Ristanovic
 */
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
class LessonTypeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LessonTypeRepository lessonTypeRepository;

    @Test
    void testSaveAndFindById() {
        LessonType lessonType = new LessonType();
        lessonType.setLessonTypeName("VIDEO");
        LessonType saved = entityManager.persistFlushFind(lessonType);

        Optional<LessonType> result = lessonTypeRepository.findById(saved.getLessonTypeId());

        assertTrue(result.isPresent());
        assertEquals("VIDEO", result.get().getLessonTypeName());
    }
}
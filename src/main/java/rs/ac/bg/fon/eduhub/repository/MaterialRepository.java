package rs.ac.bg.fon.eduhub.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.eduhub.entity.impl.Material;

/**
 * Repozitorijum za pristup podacima entiteta {@link Material}.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public interface MaterialRepository extends JpaRepository<Material, Long> {

    /**
     * Pronalazi sve nastavne materijale u okviru zadate lekcije, sortirane
     * po redosledu prikaza.
     *
     * @param lessonId identifikator lekcije
     * @return lista materijala lekcije, sortirana po {@code materialOrderIndex}
     */
    List<Material> findByLesson_LessonIdOrderByMaterialOrderIndexAsc(Long lessonId);
}
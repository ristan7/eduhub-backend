package rs.ac.bg.fon.eduhub.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.eduhub.entity.impl.Material;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByLesson_LessonIdOrderByMaterialOrderIndexAsc(Long lessonId);
}
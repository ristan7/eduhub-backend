package rs.ac.bg.fon.eduhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.eduhub.entity.lookup.LessonType;

/**
 * Repozitorijum za pristup podacima entiteta {@link LessonType}.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public interface LessonTypeRepository extends JpaRepository<LessonType, Long> {
}
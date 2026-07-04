package rs.ac.bg.fon.eduhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseLevel;

/**
 * Repozitorijum za pristup podacima entiteta {@link CourseLevel}.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public interface CourseLevelRepository extends JpaRepository<CourseLevel, Long> {
}
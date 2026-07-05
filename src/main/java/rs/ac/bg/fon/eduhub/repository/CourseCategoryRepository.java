package rs.ac.bg.fon.eduhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseCategory;

/**
 * Repozitorijum za pristup podacima entiteta {@link CourseCategory}.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Long> {
}
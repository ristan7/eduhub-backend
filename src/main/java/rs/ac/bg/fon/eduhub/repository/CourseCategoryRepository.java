package rs.ac.bg.fon.eduhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseCategory;

public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Long> {
}
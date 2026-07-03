package rs.ac.bg.fon.eduhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseLevel;

public interface CourseLevelRepository extends JpaRepository<CourseLevel, Long> {
}
package rs.ac.bg.fon.eduhub.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseStatus;

public interface CourseStatusRepository extends JpaRepository<CourseStatus, Long> {
    Optional<CourseStatus> findByCourseStatusName(String courseStatusName);
}
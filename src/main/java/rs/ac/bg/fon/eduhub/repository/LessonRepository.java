package rs.ac.bg.fon.eduhub.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.eduhub.entity.impl.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCourse_CourseIdOrderByLessonOrderIndexAsc(Long courseId);
}
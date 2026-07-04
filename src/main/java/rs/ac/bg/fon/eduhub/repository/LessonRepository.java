package rs.ac.bg.fon.eduhub.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.eduhub.entity.impl.Lesson;

/**
 * Repozitorijum za pristup podacima entiteta {@link Lesson}.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    /**
     * Pronalazi sve lekcije u okviru zadatog kursa, sortirane po
     * redosledu prikaza.
     *
     * @param courseId identifikator kursa
     * @return lista lekcija kursa, sortirana po {@code lessonOrderIndex}
     */
    List<Lesson> findByCourse_CourseIdOrderByLessonOrderIndexAsc(Long courseId);
}
package rs.ac.bg.fon.eduhub.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseStatus;

/**
 * Repozitorijum za pristup podacima entiteta {@link CourseStatus}.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public interface CourseStatusRepository extends JpaRepository<CourseStatus, Long> {

    /**
     * Pronalazi status kursa po njegovom nazivu.
     *
     * @param courseStatusName naziv statusa (npr. "DRAFT", "PUBLISHED", "ARCHIVED")
     * @return {@link Optional} sa pronađenim statusom, ili prazan ako status sa datim nazivom ne postoji
     */
    Optional<CourseStatus> findByCourseStatusName(String courseStatusName);
}
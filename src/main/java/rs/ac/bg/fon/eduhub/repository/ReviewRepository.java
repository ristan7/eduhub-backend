package rs.ac.bg.fon.eduhub.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.bg.fon.eduhub.entity.impl.Review;

/**
 * Repozitorijum za pristup podacima entiteta {@link Review}.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Pronalazi ocenu vezanu za zadatu prijavu, ukoliko postoji.
     *
     * @param enrollmentId identifikator prijave
     * @return {@link Optional} sa pronađenom ocenom, ili prazan ako prijava još nema ocenu
     */
    Optional<Review> findByEnrollment_EnrollmentId(Long enrollmentId);

    /**
     * Pronalazi sve ocene ostavljene za zadati kurs (preko svih prijava
     * studenata na taj kurs).
     *
     * @param courseId identifikator kursa
     * @return lista ocena za dati kurs
     */
    @Query("SELECT r FROM Review r WHERE r.enrollment.course.courseId = :courseId")
    List<Review> findByCourseId(@Param("courseId") Long courseId);
}
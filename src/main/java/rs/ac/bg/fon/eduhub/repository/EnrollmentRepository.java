package rs.ac.bg.fon.eduhub.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.eduhub.entity.impl.Enrollment;

/**
 * Repozitorijum za pristup podacima entiteta {@link Enrollment}.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * Pronalazi sve prijave zadatog studenta.
     *
     * @param studentId identifikator studenta
     * @return lista prijava studenta na kurseve
     */
    List<Enrollment> findByStudent_UserId(Long studentId);

    /**
     * Pronalazi prijavu studenta na konkretan kurs, ukoliko postoji.
     *
     * @param studentId identifikator studenta
     * @param courseId identifikator kursa
     * @return {@link Optional} sa pronađenom prijavom, ili prazan ako prijava ne postoji
     */
    Optional<Enrollment> findByStudent_UserIdAndCourse_CourseId(Long studentId, Long courseId);
}
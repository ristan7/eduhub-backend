package rs.ac.bg.fon.eduhub.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.eduhub.entity.lookup.EnrollmentStatus;

/**
 * Repozitorijum za pristup podacima entiteta {@link EnrollmentStatus}.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public interface EnrollmentStatusRepository extends JpaRepository<EnrollmentStatus, Long> {

    /**
     * Pronalazi status prijave po njegovom nazivu.
     *
     * @param enrollmentStatusName naziv statusa (npr. "ACTIVE", "COMPLETED")
     * @return {@link Optional} sa pronađenim statusom, ili prazan ako status sa datim nazivom ne postoji
     */
    Optional<EnrollmentStatus> findByEnrollmentStatusName(String enrollmentStatusName);
}
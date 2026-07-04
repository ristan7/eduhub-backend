package rs.ac.bg.fon.eduhub.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.eduhub.entity.lookup.EnrollmentStatus;

public interface EnrollmentStatusRepository extends JpaRepository<EnrollmentStatus, Long> {
    Optional<EnrollmentStatus> findByEnrollmentStatusName(String enrollmentStatusName);
}
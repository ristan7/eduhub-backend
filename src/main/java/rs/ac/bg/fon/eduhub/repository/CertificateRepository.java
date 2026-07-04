package rs.ac.bg.fon.eduhub.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.eduhub.entity.impl.Certificate;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    Optional<Certificate> findByEnrollment_EnrollmentId(Long enrollmentId);

    List<Certificate> findByEnrollment_Student_UserId(Long studentId);
}
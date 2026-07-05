package rs.ac.bg.fon.eduhub.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.eduhub.entity.impl.Certificate;

/**
 * Repozitorijum za pristup podacima entiteta {@link Certificate}.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    /**
     * Pronalazi sertifikat vezan za zadatu prijavu, ukoliko postoji.
     *
     * @param enrollmentId identifikator prijave
     * @return {@link Optional} sa pronađenim sertifikatom, ili prazan ako sertifikat nije izdat
     */
    Optional<Certificate> findByEnrollment_EnrollmentId(Long enrollmentId);

    /**
     * Pronalazi sve sertifikate izdate zadatom studentu (preko svih
     * njegovih prijava).
     *
     * @param studentId identifikator studenta
     * @return lista sertifikata studenta
     */
    List<Certificate> findByEnrollment_Student_UserId(Long studentId);
}
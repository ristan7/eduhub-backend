package rs.ac.bg.fon.eduhub.entity.lookup;

import jakarta.persistence.*;
import lombok.*;
import rs.ac.bg.fon.eduhub.entity.MyEntity;

/**
 * Šifarnička (lookup) klasa koja predstavlja status prijave studenta
 * na kurs (npr. ACTIVE, COMPLETED, CANCELLED, SUSPENDED).
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Entity
@Table(name = "enrollment_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "enrollmentStatusId")
@ToString
public class EnrollmentStatus implements MyEntity {

    /** Jedinstveni identifikator statusa prijave. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_status_id")
    private Long enrollmentStatusId;

    /** Naziv statusa prijave (npr. "ACTIVE", "COMPLETED"). */
    @Column(name = "enrollment_status_name", nullable = false, unique = true, length = 30)
    private String enrollmentStatusName;
}
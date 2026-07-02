package rs.ac.bg.fon.eduhub.entity.lookup;

import jakarta.persistence.*;
import lombok.*;
import rs.ac.bg.fon.eduhub.entity.MyEntity;

@Entity
@Table(name = "enrollment_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "enrollmentStatusId")
@ToString
public class EnrollmentStatus implements MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_status_id")
    private Long enrollmentStatusId;

    @Column(name = "enrollment_status_name", nullable = false, unique = true, length = 30)
    private String enrollmentStatusName;
}
package rs.ac.bg.fon.eduhub.entity.lookup;

import jakarta.persistence.*;
import lombok.*;
import rs.ac.bg.fon.eduhub.entity.MyEntity;

/**
 * Šifarnička (lookup) klasa koja predstavlja status kursa u njegovom
 * životnom ciklusu (npr. DRAFT, PUBLISHED, ARCHIVED).
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Entity
@Table(name = "course_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "courseStatusId")
@ToString
public class CourseStatus implements MyEntity {

    /** Jedinstveni identifikator statusa kursa. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_status_id")
    private Long courseStatusId;

    /** Naziv statusa (npr. "DRAFT", "PUBLISHED", "ARCHIVED"). */
    @Column(name = "course_status_name", nullable = false, unique = true, length = 30)
    private String courseStatusName;
}
package rs.ac.bg.fon.eduhub.entity.lookup;

import jakarta.persistence.*;
import lombok.*;
import rs.ac.bg.fon.eduhub.entity.MyEntity;

@Entity
@Table(name = "course_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "courseStatusId")
@ToString
public class CourseStatus implements MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_status_id")
    private Long courseStatusId;

    @Column(name = "course_status_name", nullable = false, unique = true, length = 30)
    private String courseStatusName;
}
package rs.ac.bg.fon.eduhub.entity.lookup;

import jakarta.persistence.*;
import lombok.*;
import rs.ac.bg.fon.eduhub.entity.MyEntity;

@Entity
@Table(name = "course_level")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "courseLevelId")
@ToString
public class CourseLevel implements MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_level_id")
    private Long courseLevelId;

    @Column(name = "course_level_name", nullable = false, unique = true, length = 30)
    private String courseLevelName;
}
package rs.ac.bg.fon.eduhub.entity.lookup;

import jakarta.persistence.*;
import lombok.*;
import rs.ac.bg.fon.eduhub.entity.MyEntity;

@Entity
@Table(name = "lesson_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "lessonTypeId")
@ToString
public class LessonType implements MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_type_id")
    private Long lessonTypeId;

    @Column(name = "lesson_type_name", nullable = false, unique = true, length = 30)
    private String lessonTypeName;
}
package rs.ac.bg.fon.eduhub.entity.lookup;

import jakarta.persistence.*;
import lombok.*;
import rs.ac.bg.fon.eduhub.entity.MyEntity;

/**
 * Šifarnička (lookup) klasa koja predstavlja tip lekcije
 * (npr. VIDEO, ARTICLE, QUIZ, ASSIGNMENT).
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Entity
@Table(name = "lesson_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "lessonTypeId")
@ToString
public class LessonType implements MyEntity {

    /** Jedinstveni identifikator tipa lekcije. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_type_id")
    private Long lessonTypeId;

    /** Naziv tipa lekcije (npr. "VIDEO", "QUIZ"). */
    @Column(name = "lesson_type_name", nullable = false, unique = true, length = 30)
    private String lessonTypeName;
}
package rs.ac.bg.fon.eduhub.entity.lookup;

import jakarta.persistence.*;
import lombok.*;
import rs.ac.bg.fon.eduhub.entity.MyEntity;

/**
 * Šifarnička (lookup) klasa koja predstavlja kategoriju kursa
 * (npr. PROGRAMMING, DESIGN, BUSINESS).
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Entity
@Table(name = "course_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "courseCategoryId")
@ToString
public class CourseCategory implements MyEntity {

    /** Jedinstveni identifikator kategorije kursa. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_category_id")
    private Long courseCategoryId;

    /** Naziv kategorije (npr. "PROGRAMMING", "DESIGN"). */
    @Column(name = "course_category_name", nullable = false, unique = true, length = 60)
    private String courseCategoryName;
}
package rs.ac.bg.fon.eduhub.entity.lookup;

import jakarta.persistence.*;
import lombok.*;
import rs.ac.bg.fon.eduhub.entity.MyEntity;

/**
 * Šifarnička (lookup) klasa koja predstavlja tip nastavnog materijala
 * (npr. PDF, IMAGE, LINK, PRESENTATION, VIDEO).
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Entity
@Table(name = "material_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "materialTypeId")
@ToString
public class MaterialType implements MyEntity {

    /** Jedinstveni identifikator tipa materijala. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_type_id")
    private Long materialTypeId;

    /** Naziv tipa materijala (npr. "PDF", "VIDEO"). */
    @Column(name = "material_type_name", nullable = false, unique = true, length = 30)
    private String materialTypeName;
}
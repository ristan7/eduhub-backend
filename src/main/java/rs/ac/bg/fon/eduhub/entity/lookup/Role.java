package rs.ac.bg.fon.eduhub.entity.lookup;

import jakarta.persistence.*;
import lombok.*;
import rs.ac.bg.fon.eduhub.entity.MyEntity;

/**
 * Šifarnička (lookup) klasa koja predstavlja ulogu korisnika u sistemu
 * (npr. STUDENT, INSTRUCTOR, ADMIN). Svaki korisnik ({@link rs.ac.bg.fon.eduhub.entity.impl.User})
 * ima tačno jednu dodeljenu ulogu koja određuje njegova ovlašćenja.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "roleId")
@ToString
public class Role implements MyEntity {

    /** Jedinstveni identifikator uloge. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    /** Naziv uloge (npr. "STUDENT", "INSTRUCTOR", "ADMIN"). */
    @Column(name = "role_name", nullable = false, unique = true, length = 30)
    private String roleName;
}
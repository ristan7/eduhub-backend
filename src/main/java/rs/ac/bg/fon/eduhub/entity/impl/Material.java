package rs.ac.bg.fon.eduhub.entity.impl;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import rs.ac.bg.fon.eduhub.entity.MyEntity;
import rs.ac.bg.fon.eduhub.entity.lookup.MaterialType;

/**
 * Predstavlja nastavni materijal u okviru lekcije ({@link Lesson}), poput
 * dokumenta, slike, prezentacije ili video zapisa. Materijal ima definisan
 * tip ({@link MaterialType}) i redosled prikaza unutar lekcije.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Entity
@Table(name = "material",
        indexes = {
                @Index(name = "ix_material_lesson", columnList = "lesson_id"),
                @Index(name = "ix_material_type", columnList = "material_type_id")
        })
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "materialId")
@ToString(exclude = "lesson")
public class Material implements MyEntity {

    /** Jedinstveni identifikator materijala. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id")
    private Long materialId;

    /** Naziv materijala. */
    @NotBlank
    @Size(max = 150)
    @Column(name = "material_name", nullable = false, length = 150)
    private String materialName;

    /** Redni broj materijala u okviru lekcije, koristi se za sortiranje. */
    @NotNull
    @Column(name = "material_order_index", nullable = false)
    private Integer materialOrderIndex;

    /** Tekstualni sadržaj materijala (opciono, npr. za članke). */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /** Eksterna URL adresa materijala (opciono, npr. za video ili dokument). */
    @Size(max = 500)
    @Column(name = "url", length = 500)
    private String url;

    /** Datum i vreme dodavanja materijala. */
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    /** Lekcija kojoj materijal pripada. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false, foreignKey = @ForeignKey(name = "fk_material_lesson"))
    private Lesson lesson;

    /** Tip materijala (PDF, slika, link, prezentacija, video). */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "material_type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_material_type"))
    private MaterialType materialType;

    /**
     * Postavlja datum dodavanja materijala neposredno pre upisa u bazu.
     */
    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }
}
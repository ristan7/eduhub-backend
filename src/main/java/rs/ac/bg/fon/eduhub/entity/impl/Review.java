package rs.ac.bg.fon.eduhub.entity.impl;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import rs.ac.bg.fon.eduhub.entity.MyEntity;

/**
 * Predstavlja ocenu i komentar koje student ostavlja za kurs nakon
 * prijave ({@link Enrollment}). Svaka prijava može imati najviše jednu
 * ocenu.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Entity
@Table(name = "review",
        uniqueConstraints = @UniqueConstraint(name = "uk_review_enrollment", columnNames = "enrollment_id"))
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "reviewId")
@ToString(exclude = "enrollment")
public class Review implements MyEntity {

    /** Jedinstveni identifikator ocene. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    /** Numerička ocena kursa, u opsegu od 1 do 5. */
    @NotNull
    @Min(1)
    @Max(5)
    @Column(name = "rating", nullable = false)
    private Integer rating;

    /** Tekstualni komentar studenta (opciono). */
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    /** Datum i vreme kreiranja ocene. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Prijava na koju se ocena odnosi. */
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_review_enrollment"))
    private Enrollment enrollment;

    /**
     * Postavlja datum kreiranja ocene neposredno pre upisa u bazu.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
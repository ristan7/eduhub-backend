package rs.ac.bg.fon.eduhub.entity.impl;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import rs.ac.bg.fon.eduhub.entity.MyEntity;
import rs.ac.bg.fon.eduhub.entity.lookup.EnrollmentStatus;

/**
 * Predstavlja prijavu studenta ({@link User}) na kurs ({@link Course}).
 * Prati napredak studenta kroz kurs i status prijave ({@link EnrollmentStatus}).
 * Po završetku kursa, prijava može imati povezanu ocenu ({@link Review})
 * i sertifikat ({@link Certificate}).
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Entity
@Table(name = "enrollment",
        uniqueConstraints = @UniqueConstraint(name = "uk_enrollment_student_course", columnNames = {"student_id", "course_id"}),
        indexes = {
                @Index(name = "ix_enrollment_student", columnList = "student_id"),
                @Index(name = "ix_enrollment_course", columnList = "course_id"),
                @Index(name = "ix_enrollment_status", columnList = "enrollment_status_id")
        })
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "enrollmentId")
@ToString(exclude = {"student", "course", "review", "certificate"})
public class Enrollment implements MyEntity {

    /** Jedinstveni identifikator prijave. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long enrollmentId;

    /** Datum i vreme prijave studenta na kurs. */
    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private LocalDateTime enrolledAt;

    /**
     * Procenat napretka studenta kroz kurs.
     * <p>Nedozvoljene vrednosti: {@code null}, kao i vrednosti manje od 0
     * ili veće od 100.</p>
     */
    @NotNull
    @Min(0)
    @Max(100)
    @Column(name = "progress_percentage", nullable = false)
    private Integer progressPercentage = 0;

    /** Datum i vreme završetka kursa (postavlja se kad napredak dostigne 100%). */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /** Student koji je prijavljen na kurs. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollment_student"))
    private User student;

    /** Kurs na koji je student prijavljen. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollment_course"))
    private Course course;

    /** Trenutni status prijave (ACTIVE, COMPLETED, CANCELLED, SUSPENDED). */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_status_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollment_status"))
    private EnrollmentStatus enrollmentStatus;

    /** Ocena koju je student ostavio za kurs nakon završetka (opciono). */
    @OneToOne(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;

    /** Sertifikat izdat studentu za ovu prijavu (opciono). */
    @OneToOne(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Certificate certificate;

    /**
     * Postavlja datum prijave neposredno pre upisa u bazu, ukoliko već nije postavljen.
     */
    @PrePersist
    protected void onCreate() {
        if (enrolledAt == null) {
            enrolledAt = LocalDateTime.now();
        }
    }
}
package rs.ac.bg.fon.eduhub.entity.impl;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;
import rs.ac.bg.fon.eduhub.entity.MyEntity;
import rs.ac.bg.fon.eduhub.entity.lookup.LessonType;

/**
 * Predstavlja lekciju u okviru kursa ({@link Course}). Lekcija ima
 * definisan tip ({@link LessonType}) i redosled prikaza, i sadrži skup
 * nastavnih materijala ({@link Material}).
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Entity
@Table(name = "lesson",
        indexes = {
                @Index(name = "ix_lesson_course", columnList = "course_id"),
                @Index(name = "ix_lesson_type", columnList = "lesson_type_id")
        })
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "lessonId")
@ToString(exclude = {"course", "materials"})
public class Lesson implements MyEntity {

    /** Jedinstveni identifikator lekcije. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id")
    private Long lessonId;

    /** Naslov lekcije. */
    @NotBlank
    @Size(max = 150)
    @Column(name = "lesson_title", nullable = false, length = 150)
    private String lessonTitle;

    /** Redni broj lekcije u okviru kursa, koristi se za sortiranje. */
    @NotNull
    @Column(name = "lesson_order_index", nullable = false)
    private Integer lessonOrderIndex;

    /** Datum i vreme kreiranja lekcije. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Označava da li je lekcija trenutno dostupna studentima. */
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    /** Kurs kojem lekcija pripada. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false, foreignKey = @ForeignKey(name = "fk_lesson_course"))
    private Course course;

    /** Tip lekcije (video, članak, kviz, zadatak). */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_lesson_type"))
    private LessonType lessonType;

    /** Lista nastavnih materijala u okviru lekcije, sortirana po redosledu. */
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("materialOrderIndex ASC")
    private List<Material> materials = new ArrayList<>();

    /**
     * Postavlja datum kreiranja lekcije neposredno pre upisa u bazu.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
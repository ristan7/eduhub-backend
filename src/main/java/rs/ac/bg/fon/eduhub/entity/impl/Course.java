package rs.ac.bg.fon.eduhub.entity.impl;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;
import rs.ac.bg.fon.eduhub.entity.MyEntity;
import rs.ac.bg.fon.eduhub.entity.lookup.*;

/**
 * Predstavlja kurs na EduHub platformi. Kurs pripada tačno jednoj
 * kategoriji ({@link CourseCategory}) i nivou ({@link CourseLevel}), ima
 * status u životnom ciklusu ({@link CourseStatus}), autora ({@link User})
 * i sadrži skup lekcija ({@link Lesson}) i prijava studenata
 * ({@link Enrollment}).
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Entity
@Table(name = "course",
        indexes = {
                @Index(name = "ix_course_author", columnList = "author_id"),
                @Index(name = "ix_course_category", columnList = "course_category_id"),
                @Index(name = "ix_course_level", columnList = "course_level_id"),
                @Index(name = "ix_course_status", columnList = "course_status_id")
        })
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "courseId")
@ToString(exclude = {"lessons", "enrollments"})
public class Course implements MyEntity {

    /** Jedinstveni identifikator kursa. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    /** Naslov kursa. */
    @NotBlank
    @Size(max = 150)
    @Column(name = "course_title", nullable = false, length = 150)
    private String courseTitle;

    /** Detaljan opis sadržaja i cilja kursa. */
    @NotBlank
    @Column(name = "course_description", columnDefinition = "TEXT", nullable = false)
    private String courseDescription;

    /** Datum i vreme kreiranja kursa. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Datum i vreme poslednje izmene kursa. */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** Označava da li je kurs javno objavljen. */
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;

    /** Korisnik (instruktor) koji je autor kursa. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_course_author"))
    private User author;

    /** Kategorija kojoj kurs pripada. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_course_category"))
    private CourseCategory courseCategory;

    /** Nivo težine kursa. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_level_id", nullable = false, foreignKey = @ForeignKey(name = "fk_course_level"))
    private CourseLevel courseLevel;

    /** Trenutni status kursa u životnom ciklusu (DRAFT, PUBLISHED, ARCHIVED). */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_status_id", nullable = false, foreignKey = @ForeignKey(name = "fk_course_status"))
    private CourseStatus courseStatus;

    /** Lista lekcija koje čine sadržaj kursa, sortirana po redosledu. */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("lessonOrderIndex ASC")
    private List<Lesson> lessons = new ArrayList<>();

    /** Lista prijava studenata na ovaj kurs. */
    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollments = new ArrayList<>();

    /**
     * Postavlja datume kreiranja i poslednje izmene neposredno pre upisa u bazu.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * Ažurira datum poslednje izmene neposredno pre svakog update-a u bazi.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
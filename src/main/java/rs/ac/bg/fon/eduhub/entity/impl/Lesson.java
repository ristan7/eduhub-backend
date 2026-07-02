package rs.ac.bg.fon.eduhub.entity.impl;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;
import rs.ac.bg.fon.eduhub.entity.MyEntity;
import rs.ac.bg.fon.eduhub.entity.lookup.LessonType;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id")
    private Long lessonId;

    @NotBlank
    @Size(max = 150)
    @Column(name = "lesson_title", nullable = false, length = 150)
    private String lessonTitle;

    @NotNull
    @Column(name = "lesson_order_index", nullable = false)
    private Integer lessonOrderIndex;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false, foreignKey = @ForeignKey(name = "fk_lesson_course"))
    private Course course;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_lesson_type"))
    private LessonType lessonType;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("materialOrderIndex ASC")
    private List<Material> materials = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
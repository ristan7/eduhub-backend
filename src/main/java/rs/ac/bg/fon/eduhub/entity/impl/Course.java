package rs.ac.bg.fon.eduhub.entity.impl;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;
import rs.ac.bg.fon.eduhub.entity.MyEntity;
import rs.ac.bg.fon.eduhub.entity.lookup.*;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @NotBlank
    @Size(max = 150)
    @Column(name = "course_title", nullable = false, length = 150)
    private String courseTitle;

    @NotBlank
    @Column(name = "course_description", columnDefinition = "TEXT", nullable = false)
    private String courseDescription;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_course_author"))
    private User author;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_course_category"))
    private CourseCategory courseCategory;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_level_id", nullable = false, foreignKey = @ForeignKey(name = "fk_course_level"))
    private CourseLevel courseLevel;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_status_id", nullable = false, foreignKey = @ForeignKey(name = "fk_course_status"))
    private CourseStatus courseStatus;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("lessonOrderIndex ASC")
    private List<Lesson> lessons = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
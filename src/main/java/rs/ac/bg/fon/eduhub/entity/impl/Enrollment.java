package rs.ac.bg.fon.eduhub.entity.impl;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import rs.ac.bg.fon.eduhub.entity.MyEntity;
import rs.ac.bg.fon.eduhub.entity.lookup.EnrollmentStatus;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long enrollmentId;

    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private LocalDateTime enrolledAt;

    @Column(name = "progress_percentage", nullable = false)
    private Integer progressPercentage = 0;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollment_student"))
    private User student;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollment_course"))
    private Course course;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_status_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollment_status"))
    private EnrollmentStatus enrollmentStatus;

    @OneToOne(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;

    @OneToOne(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Certificate certificate;

    @PrePersist
    protected void onCreate() {
        if (enrolledAt == null) {
            enrolledAt = LocalDateTime.now();
        }
    }
}
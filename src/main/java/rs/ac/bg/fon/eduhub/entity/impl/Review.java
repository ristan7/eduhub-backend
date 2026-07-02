package rs.ac.bg.fon.eduhub.entity.impl;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import rs.ac.bg.fon.eduhub.entity.MyEntity;

@Entity
@Table(name = "review",
        uniqueConstraints = @UniqueConstraint(name = "uk_review_enrollment", columnNames = "enrollment_id"))
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "reviewId")
@ToString(exclude = "enrollment")
public class Review implements MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_review_enrollment"))
    private Enrollment enrollment;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
package rs.ac.bg.fon.eduhub.entity.impl;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import rs.ac.bg.fon.eduhub.entity.MyEntity;
import rs.ac.bg.fon.eduhub.entity.lookup.MaterialType;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id")
    private Long materialId;

    @NotBlank
    @Size(max = 150)
    @Column(name = "material_name", nullable = false, length = 150)
    private String materialName;

    @NotNull
    @Column(name = "material_order_index", nullable = false)
    private Integer materialOrderIndex;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Size(max = 500)
    @Column(name = "url", length = 500)
    private String url;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false, foreignKey = @ForeignKey(name = "fk_material_lesson"))
    private Lesson lesson;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "material_type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_material_type"))
    private MaterialType materialType;

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }
}
package rs.ac.bg.fon.eduhub.entity.impl;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import rs.ac.bg.fon.eduhub.entity.MyEntity;

@Entity
@Table(name = "certificate",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_certificate_enrollment", columnNames = "enrollment_id"),
                @UniqueConstraint(name = "uk_certificate_code", columnNames = "code")
        })
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "certificateId")
@ToString(exclude = "enrollment")
public class Certificate implements MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Long certificateId;

    @NotBlank
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    @Size(max = 500)
    @Column(name = "certificate_url", length = 500)
    private String certificateUrl;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_certificate_enrollment"))
    private Enrollment enrollment;

    @PrePersist
    protected void onCreate() {
        this.issuedAt = LocalDateTime.now();
    }
}
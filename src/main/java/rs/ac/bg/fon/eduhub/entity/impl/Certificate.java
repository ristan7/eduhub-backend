package rs.ac.bg.fon.eduhub.entity.impl;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import rs.ac.bg.fon.eduhub.entity.MyEntity;

/**
 * Predstavlja sertifikat izdat studentu po završetku kursa, vezan za
 * konkretnu prijavu ({@link Enrollment}). Svaka prijava može imati najviše
 * jedan sertifikat, sa jedinstvenim kodom.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
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

    /** Jedinstveni identifikator sertifikata. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Long certificateId;

    /** Jedinstveni kod sertifikata, generisan pri izdavanju. */
    @NotBlank
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    /** Datum i vreme izdavanja sertifikata. */
    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    /** URL adresa na kojoj se nalazi generisani dokument sertifikata (opciono). */
    @Size(max = 500)
    @Column(name = "certificate_url", length = 500)
    private String certificateUrl;

    /** Prijava za koju je sertifikat izdat. */
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_certificate_enrollment"))
    private Enrollment enrollment;

    /**
     * Postavlja datum izdavanja sertifikata neposredno pre upisa u bazu.
     */
    @PrePersist
    protected void onCreate() {
        this.issuedAt = LocalDateTime.now();
    }
}
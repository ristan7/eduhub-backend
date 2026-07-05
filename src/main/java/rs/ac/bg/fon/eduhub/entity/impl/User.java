package rs.ac.bg.fon.eduhub.entity.impl;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;
import rs.ac.bg.fon.eduhub.entity.MyEntity;
import rs.ac.bg.fon.eduhub.entity.lookup.Role;

/**
 * Predstavlja korisnika EduHub platforme. Korisnik može imati ulogu
 * studenta, instruktora ili administratora ({@link Role}), i u zavisnosti
 * od uloge autoruje kurseve, prijavljuje se na kurseve ili prima
 * notifikacije.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Entity
@Table(name = "app_user",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_email", columnNames = "user_email"),
        indexes = @Index(name = "ix_user_role", columnList = "role_id"))
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "userId")
@ToString(exclude = {"courses", "enrollments", "notifications"})
public class User implements MyEntity {

    /** Jedinstveni identifikator korisnika. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    /** Email adresa korisnika, koristi se kao korisničko ime pri prijavi. */
    @NotBlank
    @Email
    @Column(name = "user_email", nullable = false, length = 150)
    private String userEmail;

    /** Heš lozinke korisnika (BCrypt). */
    @NotBlank
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /** Ime korisnika. */
    @NotBlank
    @Column(name = "first_name", nullable = false, length = 60)
    private String firstName;

    /** Prezime korisnika. */
    @NotBlank
    @Column(name = "last_name", nullable = false, length = 60)
    private String lastName;

    /** Označava da li je nalog korisnika aktivan (blokiran nalog ima vrednost {@code false}). */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /** Datum i vreme registracije korisnika. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Uloga dodeljena korisniku. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_role"))
    private Role role;

    /** Lista kurseva čiji je korisnik autor (relevantno za instruktore). */
    @OneToMany(mappedBy = "author")
    private List<Course> courses = new ArrayList<>();

    /** Lista prijava korisnika na kurseve (relevantno za studente). */
    @OneToMany(mappedBy = "student")
    private List<Enrollment> enrollments = new ArrayList<>();

    /** Lista notifikacija upućenih korisniku. */
    @OneToMany(mappedBy = "user")
    private List<Notification> notifications = new ArrayList<>();

    /**
     * Postavlja datum kreiranja korisnika neposredno pre upisa u bazu.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
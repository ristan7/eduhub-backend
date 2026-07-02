package rs.ac.bg.fon.eduhub.entity.impl;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;
import rs.ac.bg.fon.eduhub.entity.MyEntity;
import rs.ac.bg.fon.eduhub.entity.lookup.Role;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotBlank
    @Email
    @Column(name = "user_email", nullable = false, length = 150)
    private String userEmail;

    @NotBlank
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @NotBlank
    @Column(name = "first_name", nullable = false, length = 60)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false, length = 60)
    private String lastName;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_role"))
    private Role role;

    @OneToMany(mappedBy = "author")
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
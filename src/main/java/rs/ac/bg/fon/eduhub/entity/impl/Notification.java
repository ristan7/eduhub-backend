package rs.ac.bg.fon.eduhub.entity.impl;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import rs.ac.bg.fon.eduhub.entity.MyEntity;
import rs.ac.bg.fon.eduhub.entity.lookup.NotificationType;

/**
 * Predstavlja notifikaciju poslatu korisniku ({@link User}) platforme,
 * npr. obaveštenje o izdatom sertifikatu ili statusu kursa. Notifikacija
 * ima definisan tip ({@link NotificationType}) i status pročitanosti.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Entity
@Table(name = "notification",
        indexes = {
                @Index(name = "ix_notification_user", columnList = "user_id"),
                @Index(name = "ix_notification_type", columnList = "notification_type_id")
        })
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "notificationId")
@ToString(exclude = "user")
public class Notification implements MyEntity {

    /** Jedinstveni identifikator notifikacije. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    /** Naslov notifikacije. */
    @NotBlank
    @Size(max = 150)
    @Column(name = "notification_title", nullable = false, length = 150)
    private String notificationTitle;

    /** Tekst poruke notifikacije. */
    @NotBlank
    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    /** Datum i vreme slanja notifikacije. */
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    /** Označava da li je korisnik pročitao notifikaciju. */
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    /** Korisnik kome je notifikacija upućena. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notification_user"))
    private User user;

    /** Tip notifikacije (sistemska, o prijavi, o kursu, o sertifikatu, o oceni). */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notification_type"))
    private NotificationType notificationType;

    /**
     * Postavlja datum slanja notifikacije neposredno pre upisa u bazu.
     */
    @PrePersist
    protected void onCreate() {
        this.sentAt = LocalDateTime.now();
    }
}
package rs.ac.bg.fon.eduhub.entity.impl;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import rs.ac.bg.fon.eduhub.entity.MyEntity;
import rs.ac.bg.fon.eduhub.entity.lookup.NotificationType;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @NotBlank
    @Size(max = 150)
    @Column(name = "notification_title", nullable = false, length = 150)
    private String notificationTitle;

    @NotBlank
    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notification_user"))
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notification_type"))
    private NotificationType notificationType;

    @PrePersist
    protected void onCreate() {
        this.sentAt = LocalDateTime.now();
    }
}
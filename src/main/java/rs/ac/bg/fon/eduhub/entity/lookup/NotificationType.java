package rs.ac.bg.fon.eduhub.entity.lookup;

import jakarta.persistence.*;
import lombok.*;
import rs.ac.bg.fon.eduhub.entity.MyEntity;

/**
 * Šifarnička (lookup) klasa koja predstavlja tip notifikacije
 * (npr. SYSTEM, ENROLLMENT, COURSE, CERTIFICATE, REVIEW).
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Entity
@Table(name = "notification_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "notificationTypeId")
@ToString
public class NotificationType implements MyEntity {

    /** Jedinstveni identifikator tipa notifikacije. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_type_id")
    private Long notificationTypeId;

    /** Naziv tipa notifikacije (npr. "SYSTEM", "CERTIFICATE"). */
    @Column(name = "notification_type_name", nullable = false, unique = true, length = 30)
    private String notificationTypeName;
}
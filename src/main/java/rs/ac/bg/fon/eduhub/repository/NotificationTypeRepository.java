package rs.ac.bg.fon.eduhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.eduhub.entity.lookup.NotificationType;

/**
 * Repozitorijum za pristup podacima entiteta {@link NotificationType}.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {
}
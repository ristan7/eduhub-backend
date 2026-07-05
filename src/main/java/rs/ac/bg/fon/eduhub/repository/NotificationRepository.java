package rs.ac.bg.fon.eduhub.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.eduhub.entity.impl.Notification;

/**
 * Repozitorijum za pristup podacima entiteta {@link Notification}.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Pronalazi sve notifikacije zadatog korisnika, sortirane od
     * najnovije ka najstarijoj.
     *
     * @param userId identifikator korisnika
     * @return lista notifikacija korisnika, sortirana silazno po datumu slanja
     */
    List<Notification> findByUser_UserIdOrderBySentAtDesc(Long userId);
}
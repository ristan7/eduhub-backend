package rs.ac.bg.fon.eduhub.service;

import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.eduhub.dto.CreateNotificationRequest;
import rs.ac.bg.fon.eduhub.dto.NotificationDto;
import rs.ac.bg.fon.eduhub.entity.impl.Notification;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.NotificationType;
import rs.ac.bg.fon.eduhub.mapper.NotificationMapper;
import rs.ac.bg.fon.eduhub.repository.NotificationRepository;
import rs.ac.bg.fon.eduhub.repository.NotificationTypeRepository;
import rs.ac.bg.fon.eduhub.repository.UserRepository;

/**
 * Servis koji implementira poslovnu logiku slanja notifikacija, pregleda
 * notifikacija korisnika i označavanja notifikacija kao pročitanih
 * (SO25-SO27).
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    /**
     * Kreira servis sa svim potrebnim zavisnostima.
     *
     * @param notificationRepository repozitorijum notifikacija
     * @param notificationTypeRepository repozitorijum tipova notifikacija
     * @param userRepository repozitorijum korisnika
     * @param notificationMapper mapper za konverziju entiteta notifikacije u DTO
     */
    public NotificationService(NotificationRepository notificationRepository,
                               NotificationTypeRepository notificationTypeRepository,
                               UserRepository userRepository,
                               NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationTypeRepository = notificationTypeRepository;
        this.userRepository = userRepository;
        this.notificationMapper = notificationMapper;
    }

    /**
     * Šalje notifikaciju zadatom korisniku (SO25).
     *
     * @param request podaci o notifikaciji (primalac, naslov, poruka, tip)
     * @return DTO novokreirane notifikacije
     * @throws IllegalArgumentException ako korisnik ili tip notifikacije sa datim identifikatorom ne postoje
     */
    public NotificationDto sendNotification(CreateNotificationRequest request) {
        User recipient = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.userId()));

        NotificationType type = notificationTypeRepository.findById(request.notificationTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Notification type not found: " + request.notificationTypeId()));

        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setNotificationTitle(request.notificationTitle());
        notification.setMessage(request.message());
        notification.setNotificationType(type);
        notification.setIsRead(false);

        notificationRepository.save(notification);
        return notificationMapper.toDto(notification);
    }

    /**
     * Vraća listu svih notifikacija trenutno prijavljenog korisnika,
     * sortiranu od najnovije ka najstarijoj (SO26).
     *
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return lista notifikacija korisnika
     */
    public List<NotificationDto> getMyNotifications(Authentication authentication) {
        User user = currentUser(authentication);
        return notificationRepository.findByUser_UserIdOrderBySentAtDesc(user.getUserId())
                .stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    /**
     * Označava notifikaciju kao pročitanu (SO27). Dozvoljeno samo
     * korisniku kome je notifikacija upućena.
     *
     * @param notificationId identifikator notifikacije koja se označava
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return DTO ažurirane notifikacije
     * @throws IllegalArgumentException ako notifikacija sa datim identifikatorom ne postoji
     * @throws AccessDeniedException ako notifikacija nije upućena trenutno prijavljenom korisniku
     */
    public NotificationDto markAsRead(Long notificationId, Authentication authentication) {
        User user = currentUser(authentication);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));

        if (notification.getUser() == null || !notification.getUser().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("You can only mark your own notifications as read.");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
        return notificationMapper.toDto(notification);
    }

    /**
     * Pronalazi entitet korisnika na osnovu email adrese iz autentifikacije.
     *
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return pronađeni entitet korisnika
     * @throws IllegalStateException ako autentifikovani korisnik neočekivano ne postoji u bazi
     */
    private User currentUser(Authentication authentication) {
        return userRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));
    }
}
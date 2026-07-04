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

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationTypeRepository notificationTypeRepository,
                               UserRepository userRepository,
                               NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationTypeRepository = notificationTypeRepository;
        this.userRepository = userRepository;
        this.notificationMapper = notificationMapper;
    }

    // SO25 - Slanje notifikacije korisniku
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

    // SO26 - Pregled notifikacija korisnika
    public List<NotificationDto> getMyNotifications(Authentication authentication) {
        User user = currentUser(authentication);
        return notificationRepository.findByUser_UserIdOrderBySentAtDesc(user.getUserId())
                .stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    // SO27 - Označavanje notifikacije kao pročitane
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

    private User currentUser(Authentication authentication) {
        return userRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));
    }
}
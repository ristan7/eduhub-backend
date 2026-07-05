package rs.ac.bg.fon.eduhub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
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
 * JUnit testovi za servis {@link NotificationService}, uz mokovanje svih
 * zavisnosti (SO25-SO27).
 *
 * @author Mihajlo Ristanovic
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationTypeRepository notificationTypeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationMapper notificationMapper;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationRepository, notificationTypeRepository,
                userRepository, notificationMapper);
    }

    @Test
    void testSendNotificationSuccess() {
        CreateNotificationRequest request = new CreateNotificationRequest(1L, "Naslov", "Poruka", 1L);

        User recipient = new User();
        recipient.setUserId(1L);
        NotificationType type = new NotificationType(1L, "SYSTEM");
        NotificationDto expectedDto = new NotificationDto(1L, "Naslov", "Poruka", null, false, 1L, 1L, "SYSTEM");

        when(userRepository.findById(1L)).thenReturn(Optional.of(recipient));
        when(notificationTypeRepository.findById(1L)).thenReturn(Optional.of(type));
        when(notificationMapper.toDto(any(Notification.class))).thenReturn(expectedDto);

        NotificationDto result = notificationService.sendNotification(request);

        assertEquals(expectedDto, result);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testSendNotificationUserNotFound() {
        CreateNotificationRequest request = new CreateNotificationRequest(99L, "Naslov", "Poruka", 1L);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> notificationService.sendNotification(request));
    }

    @Test
    void testSendNotificationTypeNotFound() {
        CreateNotificationRequest request = new CreateNotificationRequest(1L, "Naslov", "Poruka", 99L);
        User recipient = new User();
        recipient.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(recipient));
        when(notificationTypeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> notificationService.sendNotification(request));
    }

    @Test
    void testGetMyNotifications() {
        Authentication authentication = mock(Authentication.class);
        User user = new User();
        user.setUserId(1L);

        Notification notification = new Notification();
        NotificationDto dto = new NotificationDto(1L, "Naslov", "Poruka", null, false, 1L, 1L, "SYSTEM");

        when(authentication.getName()).thenReturn("user@eduhub.com");
        when(userRepository.findByUserEmail("user@eduhub.com")).thenReturn(Optional.of(user));
        when(notificationRepository.findByUser_UserIdOrderBySentAtDesc(1L)).thenReturn(List.of(notification));
        when(notificationMapper.toDto(notification)).thenReturn(dto);

        List<NotificationDto> result = notificationService.getMyNotifications(authentication);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void testMarkAsReadSuccess() {
        Long notificationId = 1L;
        Authentication authentication = mock(Authentication.class);

        User user = new User();
        user.setUserId(1L);

        Notification notification = new Notification();
        notification.setNotificationId(notificationId);
        notification.setUser(user);
        notification.setIsRead(false);

        NotificationDto expectedDto = new NotificationDto(1L, "Naslov", "Poruka", null, true, 1L, 1L, "SYSTEM");

        when(authentication.getName()).thenReturn("user@eduhub.com");
        when(userRepository.findByUserEmail("user@eduhub.com")).thenReturn(Optional.of(user));
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notificationMapper.toDto(notification)).thenReturn(expectedDto);

        NotificationDto result = notificationService.markAsRead(notificationId, authentication);

        assertEquals(true, notification.getIsRead());
        assertEquals(expectedDto, result);
    }

    @Test
    void testMarkAsReadForbiddenForNonOwner() {
        Long notificationId = 1L;
        Authentication authentication = mock(Authentication.class);

        User owner = new User();
        owner.setUserId(1L);
        User intruder = new User();
        intruder.setUserId(2L);

        Notification notification = new Notification();
        notification.setNotificationId(notificationId);
        notification.setUser(owner);

        when(authentication.getName()).thenReturn("intruder@eduhub.com");
        when(userRepository.findByUserEmail("intruder@eduhub.com")).thenReturn(Optional.of(intruder));
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        assertThrows(AccessDeniedException.class, () -> notificationService.markAsRead(notificationId, authentication));
    }
}
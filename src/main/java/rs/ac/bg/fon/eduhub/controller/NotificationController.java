package rs.ac.bg.fon.eduhub.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.bg.fon.eduhub.dto.CreateNotificationRequest;
import rs.ac.bg.fon.eduhub.dto.NotificationDto;
import rs.ac.bg.fon.eduhub.service.NotificationService;

/**
 * REST kontroler za slanje notifikacija, pregled notifikacija korisnika
 * i označavanje notifikacija kao pročitanih (SO25-SO27). Slanje
 * notifikacije zahteva ulogu INSTRUCTOR ili ADMIN.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Kreira kontroler sa injektovanim servisom za notifikacije.
     *
     * @param notificationService servis koji implementira poslovnu logiku notifikacija
     */
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Šalje notifikaciju zadatom korisniku (SO25).
     *
     * @param request podaci o notifikaciji (primalac, naslov, poruka, tip)
     * @return HTTP 201 sa podacima novokreirane notifikacije
     */
    @PostMapping
    public ResponseEntity<NotificationDto> sendNotification(@Valid @RequestBody CreateNotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.sendNotification(request));
    }

    /**
     * Vraća listu svih notifikacija trenutno prijavljenog korisnika
     * (SO26).
     *
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return HTTP 200 sa listom notifikacija korisnika
     */
    @GetMapping("/me")
    public ResponseEntity<List<NotificationDto>> getMyNotifications(Authentication authentication) {
        return ResponseEntity.ok(notificationService.getMyNotifications(authentication));
    }

    /**
     * Označava notifikaciju kao pročitanu (SO27).
     *
     * @param id identifikator notifikacije koja se označava
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return HTTP 200 sa podacima ažurirane notifikacije
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationDto> markAsRead(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(notificationService.markAsRead(id, authentication));
    }
}
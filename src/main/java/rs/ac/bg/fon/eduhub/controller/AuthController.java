package rs.ac.bg.fon.eduhub.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.bg.fon.eduhub.dto.AuthResponse;
import rs.ac.bg.fon.eduhub.dto.LoginRequest;
import rs.ac.bg.fon.eduhub.dto.RegisterRequest;
import rs.ac.bg.fon.eduhub.dto.UserDto;
import rs.ac.bg.fon.eduhub.service.AuthService;

/**
 * REST kontroler za registraciju, prijavu i odjavu korisnika (SO1-SO3).
 * Svi endpointi ovog kontrolera su javno dostupni, bez potrebe za
 * prethodnom autentifikacijom.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Kreira kontroler sa injektovanim servisom za autentifikaciju.
     *
     * @param authService servis koji implementira poslovnu logiku autentifikacije
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registruje novog korisnika na platformi (SO1).
     *
     * @param request podaci o novom korisniku
     * @return HTTP 201 sa podacima o registrovanom korisniku
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    /**
     * Prijavljuje korisnika na sistem i vraća JWT token (SO2).
     *
     * @param request email i lozinka korisnika
     * @return HTTP 200 sa JWT tokenom i podacima o korisniku
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Odjavljuje korisnika sa sistema (SO3). Pošto je autentifikacija
     * zasnovana na JWT tokenima bez stanja na serveru, odjava se svodi
     * na brisanje tokena na klijentskoj strani.
     *
     * @return HTTP 200 bez tela odgovora
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }
}
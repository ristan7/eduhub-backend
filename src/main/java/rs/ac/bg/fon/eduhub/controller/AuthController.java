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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // SO1 - Registracija korisnika
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    // SO2 - Prijava korisnika na sistem
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // SO3 - Odjava korisnika sa sistema
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // JWT je stateless - odjava se vrši brisanjem tokena na klijentskoj strani
        return ResponseEntity.ok().build();
    }
}
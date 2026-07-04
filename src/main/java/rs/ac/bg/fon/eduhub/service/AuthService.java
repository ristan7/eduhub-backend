package rs.ac.bg.fon.eduhub.service;

import java.util.Map;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.eduhub.dto.AuthResponse;
import rs.ac.bg.fon.eduhub.dto.LoginRequest;
import rs.ac.bg.fon.eduhub.dto.RegisterRequest;
import rs.ac.bg.fon.eduhub.dto.UserDto;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.Role;
import rs.ac.bg.fon.eduhub.mapper.UserMapper;
import rs.ac.bg.fon.eduhub.repository.RoleRepository;
import rs.ac.bg.fon.eduhub.repository.UserRepository;
import rs.ac.bg.fon.eduhub.security.JwtService;

/**
 * Servis koji implementira poslovnu logiku registracije i prijave
 * korisnika (SO1, SO2). Odjava (SO3) se, pošto je autentifikacija
 * zasnovana na JWT tokenima bez stanja na serveru, obavlja isključivo na
 * klijentskoj strani i ne zahteva poslovnu logiku u ovom servisu.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Service
public class AuthService {

    private static final String DEFAULT_ROLE = "STUDENT";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Kreira servis sa svim potrebnim zavisnostima.
     *
     * @param authenticationManager Spring Security menadžer za autentifikaciju
     * @param jwtService servis za generisanje JWT tokena
     * @param userRepository repozitorijum korisnika
     * @param roleRepository repozitorijum uloga
     * @param passwordEncoder enkoder za heširanje lozinki
     * @param userMapper mapper za konverziju entiteta korisnika u DTO
     */
    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    /**
     * Registruje novog korisnika sa podrazumevanom ulogom STUDENT (SO1).
     * Lozinka se pre čuvanja hešira, a email adresa mora biti jedinstvena.
     *
     * @param request podaci o novom korisniku
     * @return DTO sa podacima novoregistrovanog korisnika
     * @throws IllegalArgumentException ako korisnik sa datom email adresom već postoji
     * @throws IllegalStateException ako podrazumevana uloga STUDENT ne postoji u bazi
     */
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByUserEmail(request.userEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.userEmail());
        }

        Role studentRole = roleRepository.findByRoleName(DEFAULT_ROLE)
                .orElseThrow(() -> new IllegalStateException("Default role STUDENT not found in database."));

        User user = new User();
        user.setUserEmail(request.userEmail());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setIsActive(true);
        user.setRole(studentRole);

        userRepository.save(user);
        return userMapper.toDto(user);
    }

    /**
     * Prijavljuje korisnika na sistem proverom email adrese i lozinke, i
     * generiše JWT token za dalju autentifikaciju (SO2).
     *
     * @param request podaci za prijavu (email i lozinka)
     * @return odgovor sa JWT tokenom i podacima o korisniku
     * @throws org.springframework.security.core.AuthenticationException ako email/lozinka nisu ispravni ili je nalog blokiran
     * @throws IllegalStateException ako autentifikovani korisnik neočekivano ne postoji u bazi
     */
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.userEmail(), request.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUserEmail(request.userEmail())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));

        String token = jwtService.generate(userDetails, Map.of(
                "userId", user.getUserId(),
                "role", user.getRole() != null ? user.getRole().getRoleName() : DEFAULT_ROLE
        ));

        return new AuthResponse(token, userMapper.toDto(user));
    }
}
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

@Service
public class AuthService {

    private static final String DEFAULT_ROLE = "STUDENT";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

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
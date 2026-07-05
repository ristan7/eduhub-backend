package rs.ac.bg.fon.eduhub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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
 * JUnit testovi za servis {@link AuthService}, uz mokovanje svih
 * zavisnosti (SO1, SO2).
 *
 * @author Mihajlo Ristanovic
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(authenticationManager, jwtService, userRepository,
                roleRepository, passwordEncoder, userMapper);
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest("student@eduhub.com", "password123", "Petar", "Nikolic");
        Role studentRole = new Role(1L, "STUDENT");
        UserDto expectedDto = new UserDto(1L, "student@eduhub.com", "Petar", "Nikolic", "STUDENT", true, null);

        when(userRepository.existsByUserEmail("student@eduhub.com")).thenReturn(false);
        when(roleRepository.findByRoleName("STUDENT")).thenReturn(Optional.of(studentRole));
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userMapper.toDto(any(User.class))).thenReturn(expectedDto);

        UserDto result = authService.register(request);

        assertEquals(expectedDto, result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("student@eduhub.com", "password123", "Petar", "Nikolic");
        when(userRepository.existsByUserEmail("student@eduhub.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
    }

    @Test
    void testRegisterDefaultRoleNotFound() {
        RegisterRequest request = new RegisterRequest("student@eduhub.com", "password123", "Petar", "Nikolic");
        when(userRepository.existsByUserEmail("student@eduhub.com")).thenReturn(false);
        when(roleRepository.findByRoleName("STUDENT")).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> authService.register(request));
    }

    @Test
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest("student@eduhub.com", "password123");

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        User user = new User();
        user.setUserId(1L);
        user.setUserEmail("student@eduhub.com");
        Role role = new Role(1L, "STUDENT");
        user.setRole(role);

        UserDto expectedDto = new UserDto(1L, "student@eduhub.com", "Petar", "Nikolic", "STUDENT", true, null);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByUserEmail("student@eduhub.com")).thenReturn(Optional.of(user));
        when(jwtService.generate(eq(userDetails), anyMap())).thenReturn("jwt-token-123");
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        AuthResponse response = authService.login(request);

        assertEquals("jwt-token-123", response.token());
        assertEquals(expectedDto, response.user());
    }
}
package rs.ac.bg.fon.eduhub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import rs.ac.bg.fon.eduhub.security.JwtAuthFilter;

/**
 * Centralna konfiguracija Spring Security-ja: definiše koje su rute
 * javno dostupne, koje zahtevaju samo autentifikaciju, a koje zahtevaju
 * konkretnu ulogu (INSTRUCTOR, ADMIN); registruje JWT filter i konfiguriše
 * enkodovanje lozinki. Sesija je stateless (JWT), a CSRF zaštita je
 * isključena jer nije relevantna za stateless REST API.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Kreira konfiguraciju sa injektovanim zavisnostima.
     *
     * @param jwtAuthFilter filter koji obrađuje JWT tokene iz zahteva
     * @param userDetailsService servis za učitavanje podataka korisnika
     */
    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Definiše lanac bezbednosnih filtera i pravila autorizacije za sve
     * rute aplikacije.
     *
     * @param http Spring Security graditelj konfiguracije
     * @return konfigurisani {@link SecurityFilterChain}
     * @throws Exception ako dođe do greške pri izgradnji konfiguracije
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/logout").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/courses/*/lessons").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/courses/*/reviews").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/courses", "/api/courses/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/courses/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/courses/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/courses/*/lessons").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/lessons/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/lessons/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/lessons/*/materials").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/lessons/*/materials").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers("/api/enrollments/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/enrollments/*/review").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/enrollments/*/certificate").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/certificates/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/notifications").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/notifications/me").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/notifications/*/read").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Definiše provajdera autentifikacije koji koristi
     * {@link UserDetailsService} za učitavanje korisnika i BCrypt za
     * proveru lozinki.
     *
     * @return konfigurisani {@link AuthenticationProvider}
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Definiše enkoder lozinki zasnovan na BCrypt algoritmu.
     *
     * @return {@link PasswordEncoder} instanca
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Izlaže Spring Security-jev {@link AuthenticationManager} kao bean,
     * potreban za ručnu autentifikaciju korisnika pri prijavi
     * ({@link rs.ac.bg.fon.eduhub.service.AuthService#login}).
     *
     * @param config Spring Security konfiguracija autentifikacije
     * @return konfigurisani {@link AuthenticationManager}
     * @throws Exception ako dođe do greške pri izgradnji menadžera
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
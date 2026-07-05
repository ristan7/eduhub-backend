package rs.ac.bg.fon.eduhub.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Servletski filter koji se izvršava jednom po zahtevu i proverava
 * prisustvo i validnost JWT tokena u {@code Authorization} zaglavlju
 * (u formatu {@code Bearer <token>}). Ukoliko je token validan,
 * postavlja odgovarajuću autentifikaciju u {@link SecurityContextHolder},
 * čime dalji tok obrade zahteva korisnika tretira kao prijavljenog.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Kreira filter sa injektovanim zavisnostima.
     *
     * @param jwtService servis za validaciju i čitanje podataka iz JWT tokena
     * @param userDetailsService servis za učitavanje podataka korisnika
     */
    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Proverava {@code Authorization} zaglavlje zahteva i, ukoliko sadrži
     * validan JWT token, postavlja autentifikaciju korisnika u
     * {@link SecurityContextHolder} pre nego što zahtev prosledi dalje
     * kroz filter lanac.
     *
     * @param request HTTP zahtev
     * @param response HTTP odgovor
     * @param chain lanac filtera kroz koji se zahtev dalje prosleđuje
     * @throws ServletException u slučaju greške servleta
     * @throws IOException u slučaju greške pri obradi ulazno/izlaznih tokova
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        try {
            String email = jwtService.extractEmail(token);
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if (jwtService.isValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (Exception ignored) {
            // Nevalidan token - nastavljamo bez autentifikacije, SecurityConfig odlučuje da li je ruta zaštićena
        }

        chain.doFilter(request, response);
    }
}
package rs.ac.bg.fon.eduhub.security;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.repository.UserRepository;

/**
 * Implementacija Spring Security-jevog {@link UserDetailsService} koja
 * učitava korisničke podatke iz baze na osnovu email adrese. Ulogu
 * korisnika prevodi u Spring Security autoritet oblika
 * {@code ROLE_<NAZIV_ULOGE>}, a blokiran nalog ({@code isActive = false})
 * se prijavljuje kao onemogućen ({@code disabled}).
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Kreira servis sa injektovanim repozitorijumom korisnika.
     *
     * @param userRepository repozitorijum korisnika
     */
    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Učitava podatke korisnika po email adresi i prevodi ih u Spring
     * Security-jev {@link UserDetails} objekat.
     *
     * @param userEmail email adresa korisnika (koristi se kao korisničko ime)
     * @return {@link UserDetails} sa email-om, heširanom lozinkom, autoritetom uloge i statusom aktivnosti naloga
     * @throws UsernameNotFoundException ako korisnik sa datom email adresom ne postoji
     */
    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        String roleName = user.getRole() != null ? user.getRole().getRoleName() : "STUDENT";
        String authority = "ROLE_" + roleName.toUpperCase();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserEmail())
                .password(user.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(authority)))
                .disabled(!Boolean.TRUE.equals(user.getIsActive()))
                .build();
    }
}
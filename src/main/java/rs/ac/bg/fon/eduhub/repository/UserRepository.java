package rs.ac.bg.fon.eduhub.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.bg.fon.eduhub.entity.impl.User;

/**
 * Repozitorijum za pristup podacima entiteta {@link User}.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Pronalazi korisnika po email adresi, uz eksplicitno učitavanje
     * povezane uloge ({@code JOIN FETCH}) kako bi se izbegao
     * {@code LazyInitializationException} pri pristupu ulozi van
     * Hibernate sesije (npr. u {@link rs.ac.bg.fon.eduhub.security.JwtAuthFilter}).
     *
     * @param userEmail email adresa korisnika
     * @return {@link Optional} sa pronađenim korisnikom, ili prazan ako korisnik ne postoji
     */
    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.userEmail = :userEmail")
    Optional<User> findByUserEmail(@Param("userEmail") String userEmail);

    /**
     * Proverava da li postoji korisnik sa datom email adresom.
     *
     * @param userEmail email adresa koja se proverava
     * @return {@code true} ako korisnik sa datim email-om već postoji, inače {@code false}
     */
    boolean existsByUserEmail(String userEmail);

    /**
     * Broji korisnike koji imaju dodeljenu ulogu sa datim nazivom.
     *
     * @param roleName naziv uloge (npr. "STUDENT", "INSTRUCTOR", "ADMIN")
     * @return broj korisnika sa datom ulogom
     */
    long countByRole_RoleName(String roleName);
}
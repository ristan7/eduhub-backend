package rs.ac.bg.fon.eduhub.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.eduhub.entity.lookup.Role;

/**
 * Repozitorijum za pristup podacima entiteta {@link Role}.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Pronalazi ulogu po njenom nazivu.
     *
     * @param roleName naziv uloge (npr. "STUDENT", "INSTRUCTOR", "ADMIN")
     * @return {@link Optional} sa pronađenom ulogom, ili prazan ako uloga sa datim nazivom ne postoji
     */
    Optional<Role> findByRoleName(String roleName);
}
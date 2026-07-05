package rs.ac.bg.fon.eduhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.eduhub.entity.lookup.MaterialType;

/**
 * Repozitorijum za pristup podacima entiteta {@link MaterialType}.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public interface MaterialTypeRepository extends JpaRepository<MaterialType, Long> {
}
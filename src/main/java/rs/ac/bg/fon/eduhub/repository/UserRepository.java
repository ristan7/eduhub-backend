package rs.ac.bg.fon.eduhub.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.bg.fon.eduhub.entity.impl.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.userEmail = :userEmail")
    Optional<User> findByUserEmail(@Param("userEmail") String userEmail);

    boolean existsByUserEmail(String userEmail);

    long countByRole_RoleName(String roleName);
}
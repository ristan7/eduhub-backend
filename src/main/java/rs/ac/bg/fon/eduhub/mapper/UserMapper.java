package rs.ac.bg.fon.eduhub.mapper;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.eduhub.dto.UserDto;
import rs.ac.bg.fon.eduhub.entity.impl.User;

/**
 * Konvertuje {@link User} entitete u odgovarajuće {@link UserDto} objekte.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Component
public class UserMapper {

    /**
     * Mapira entitet korisnika u DTO za slanje klijentu.
     *
     * @param user entitet korisnika, može biti {@code null}
     * @return {@link UserDto} sa podacima korisnika, ili {@code null} ako je ulazni entitet {@code null}
     */
    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getUserId(),
                user.getUserEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole() != null ? user.getRole().getRoleName() : null,
                user.getIsActive(),
                user.getCreatedAt()
        );
    }
}
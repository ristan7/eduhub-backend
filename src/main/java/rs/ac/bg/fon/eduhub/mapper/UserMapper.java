package rs.ac.bg.fon.eduhub.mapper;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.eduhub.dto.UserDto;
import rs.ac.bg.fon.eduhub.entity.impl.User;

@Component
public class UserMapper {

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
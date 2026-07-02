package rs.ac.bg.fon.eduhub.dto;

import java.time.LocalDateTime;

public record UserDto(
        Long userId,
        String userEmail,
        String firstName,
        String lastName,
        String roleName,
        Boolean isActive,
        LocalDateTime createdAt
) {
}
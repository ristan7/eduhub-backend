package rs.ac.bg.fon.eduhub.dto;

public record AuthResponse(
        String token,
        UserDto user
) {
}
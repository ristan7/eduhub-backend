package rs.ac.bg.fon.eduhub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email is required.")
        @Email(message = "Email must be valid.")
        String userEmail,

        @NotBlank(message = "Password is required.")
        String password
) {
}
package rs.ac.bg.fon.eduhub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Email is required.")
        @Email(message = "Email must be valid.")
        String userEmail,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, message = "Password must be at least 8 characters.")
        String password,

        @NotBlank(message = "First name is required.")
        @Size(max = 60)
        String firstName,

        @NotBlank(message = "Last name is required.")
        @Size(max = 60)
        String lastName
) {
}
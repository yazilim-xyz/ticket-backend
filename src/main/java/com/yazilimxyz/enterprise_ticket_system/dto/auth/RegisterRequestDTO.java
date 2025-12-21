package com.yazilimxyz.enterprise_ticket_system.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(

                @NotBlank(message = "Name is required") @Size(min = 2, max = 50, message = "Name must be 2-50 characters") String name,

                @NotBlank(message = "Surname is required") @Size(min = 2, max = 50, message = "Surname must be 2-50 characters") String surname,

                @NotBlank(message = "Email is required") @Email(message = "Email must be valid") @Size(max = 150, message = "Email max length is 150") String email,

                @NotBlank(message = "Password is required") @Size(min = 8, max = 64, message = "Password must be 8-64 characters") String password) {
}

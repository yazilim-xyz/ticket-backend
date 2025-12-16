package com.example.ticketing.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class AdminUpdateUserRequest {

    @Size(min = 2, max = 100)
    private String fullName;

    @Email
    private String email;

    private Boolean enabled;

    private Set<String> roles;
}

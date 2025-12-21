package com.yazilimxyz.enterprise_ticket_system.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {

    @NotBlank(message = "Eski şifre boş olamaz")
    private String oldPassword;

    @NotBlank(message = "Yeni şifre boş olamaz")
    @Size(min = 8, message = "Yeni şifre en az 8 karakter olmalıdır")
    private String newPassword;

    @NotBlank(message = "Şifre doğrulama boş olamaz")
    private String confirmPassword;
}

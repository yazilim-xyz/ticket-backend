package com.yazilimxyz.enterprise_ticket_system.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileDTO {

    @Size(min = 2, max = 50, message = "İsim 2-50 karakter arasında olmalıdır")
    private String name;

    @Size(min = 2, max = 50, message = "Soyisim 2-50 karakter arasında olmalıdır")
    private String surname;

    @Email(message = "Geçerli bir email adresi giriniz")
    private String email;

    @Size(max = 20, message = "Telefon numarası 20 karakteri geçemez")
    private String phoneNumber;
}

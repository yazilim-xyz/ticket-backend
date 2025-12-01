package com.yazilimxyz.enterprise_ticket_system.dto.auth;

public record RegisterRequestDTO(
        String fullName,
        String email,
        String password
) {}
//Bu DTO, kullanıcı kayıt olurken front-end’in bize gönderdiği veri.
//yani kullanıcının frontend’den gönderdiği veriyi taşır.

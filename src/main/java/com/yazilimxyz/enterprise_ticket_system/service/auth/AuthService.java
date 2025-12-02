package com.yazilimxyz.enterprise_ticket_system.service.auth;

import com.yazilimxyz.enterprise_ticket_system.dto.auth.*;

public interface AuthService {
//Bu servis katmanının hangi işlevlere sahip olduğunu belirtir.
    RegisterResponseDTO register(RegisterRequestDTO request);

    LoginResponseDTO login(LoginRequestDTO request);

    LoginResponseDTO refreshToken(RefreshTokenRequestDTO request);

    void logout(Long userId);
}


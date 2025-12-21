package com.yazilimxyz.enterprise_ticket_system.service.user;

import com.yazilimxyz.enterprise_ticket_system.dto.user.ChangePasswordDTO;
import com.yazilimxyz.enterprise_ticket_system.dto.user.UpdateProfileDTO;
import com.yazilimxyz.enterprise_ticket_system.dto.user.UpdateProfileResponseDTO;
import com.yazilimxyz.enterprise_ticket_system.dto.user.UserListItemDto;
import com.yazilimxyz.enterprise_ticket_system.entities.User;

import java.util.List;

public interface UserService {

    /**
     * Tüm kullanıcıları listele
     */
    List<UserListItemDto> getAllUsers();

    /**
     * Sadece aktif kullanıcıları listele
     */
    List<UserListItemDto> getActiveUsers();

    /**
     * Kullanıcı profilini güncelle (isim, soyisim, email, telefon)
     */
    UpdateProfileResponseDTO updateProfile(Long userId, UpdateProfileDTO dto);

    /**
     * Şifre değiştir (eski şifre doğrulaması yapılır)
     */
    void changePassword(Long userId, ChangePasswordDTO dto);
}

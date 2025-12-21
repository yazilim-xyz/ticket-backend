package com.yazilimxyz.enterprise_ticket_system.service.user;

import com.yazilimxyz.enterprise_ticket_system.dto.user.UserListItemDto;

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
}

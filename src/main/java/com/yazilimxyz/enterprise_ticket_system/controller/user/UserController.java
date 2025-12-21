package com.yazilimxyz.enterprise_ticket_system.controller.user;

import com.yazilimxyz.enterprise_ticket_system.dto.user.UserListItemDto;
import com.yazilimxyz.enterprise_ticket_system.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Sadece aktif(soft delete olmamış) kullanıcıları listele.
     */
    @GetMapping
    public ResponseEntity<List<UserListItemDto>> getActiveUsers() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }
}

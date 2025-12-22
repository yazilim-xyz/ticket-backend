package com.yazilimxyz.enterprise_ticket_system.service.admin;

import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminTicketResponseDto;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminUserCreateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminUserResponseDto;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminUserUpdateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.ChangeUserRoleRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.ChangeUserStatusRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.UserActivityDto;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.UserStatsDto;
import com.yazilimxyz.enterprise_ticket_system.entities.Role;
import com.yazilimxyz.enterprise_ticket_system.entities.Ticket;
import com.yazilimxyz.enterprise_ticket_system.entities.TicketActivityLog;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketStatus;
import com.yazilimxyz.enterprise_ticket_system.exception.BadRequestException;
import com.yazilimxyz.enterprise_ticket_system.exception.NotFoundException;
import com.yazilimxyz.enterprise_ticket_system.mapper.AdminTicketMapper;
import com.yazilimxyz.enterprise_ticket_system.mapper.AdminUserMapper;
import com.yazilimxyz.enterprise_ticket_system.Repositories.TicketActivityLogRepository;
import com.yazilimxyz.enterprise_ticket_system.Repositories.TicketRepository;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepo;
    private final TicketRepository ticketRepo;
    private final TicketActivityLogRepository activityRepo;
    private final AdminTicketMapper ticketMapper;
    private final AdminUserMapper mapper;
    private final PasswordEncoder encoder;

    @Override
    public Page<AdminUserResponseDto> getUsers(int page, int size) {
        Page<User> users = userRepo.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
        return users.map(mapper::toDto);
    }

    @Override
    public AdminUserResponseDto getUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return mapper.toDto(user);
    }

    @Override
    public AdminUserResponseDto createUser(AdminUserCreateRequest req) {
        if (req.getEmail() == null || req.getEmail().isBlank()) {
            throw new BadRequestException("Email is required");
        }
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already in use");
        }
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new BadRequestException("Password is required");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setFullName(req.getFullName());

        user.setPasswordHash(encoder.encode(req.getPassword()));

        Role role = req.getRole() != null ? parseRole(req.getRole()) : Role.USER;
        user.setRole(role);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return mapper.toDto(userRepo.save(user));
    }

    @Override
    public AdminUserResponseDto updateUser(Long id, AdminUserUpdateRequest req) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        mapper.updateFromRequest(req, user);
        user.setUpdatedAt(LocalDateTime.now());

        return mapper.toDto(userRepo.save(user));
    }

    @Override
    public void changeUserStatus(Long id, ChangeUserStatusRequest req) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        if (req.getStatus() == null) {
            throw new BadRequestException("Status is required");
        }

        String status = req.getStatus().trim().toUpperCase();
        boolean activate;
        if ("ACTIVE".equals(status)) {
            activate = true;
        } else if ("DISABLED".equals(status)) {
            activate = false;
        } else {
            throw new BadRequestException("Unknown status: " + req.getStatus());
        }
        user.setActive(activate);
        user.setUpdatedAt(LocalDateTime.now());
        userRepo.save(user);
    }

    @Override
    public void changeUserRole(Long id, ChangeUserRoleRequest req) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        user.setRole(parseRole(req.getRole()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepo.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        // Soft delete: hesabı devre dışı bırak
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepo.save(user);
    }

    @Override
    public Page<AdminTicketResponseDto> getUserTickets(Long userId, int page, int size) {
        // kullanıcı var mı kontrolü
        userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Page<Ticket> tickets = ticketRepo.findByCreatedByIdOrAssignedToId(
                userId, userId, PageRequest.of(page, size, Sort.by("id").descending()));
        return tickets.map(ticketMapper::toDto);
    }

    @Override
    public UserStatsDto getUserStats(Long userId) {
        userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        long createdCount = ticketRepo.countByCreatedById(userId);
        long assignedCount = ticketRepo.countByAssignedToId(userId);

        long openCount = countByStatus(userId, TicketStatus.OPEN);
        long inProgressCount = countByStatus(userId, TicketStatus.IN_PROGRESS);
        long resolvedCount = countByStatus(userId, TicketStatus.RESOLVED);
        long closedCount = countByStatus(userId, TicketStatus.CLOSED);
        long cancelledCount = countByStatus(userId, TicketStatus.CANCELLED);

        return new UserStatsDto(
                createdCount,
                assignedCount,
                openCount,
                inProgressCount,
                resolvedCount,
                closedCount,
                cancelledCount);
    }

    private long countByStatus(Long userId, TicketStatus status) {
        return ticketRepo.countByCreatedByIdAndStatus(userId, status)
                + ticketRepo.countByAssignedToIdAndStatus(userId, status);
    }

    @Override
    public Page<UserActivityDto> getUserActivity(Long userId, int page, int size) {
        userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Page<TicketActivityLog> logs = activityRepo.findByUserId(
                userId, PageRequest.of(page, size, Sort.by("createdAt").descending()));

        return logs.map(log -> new UserActivityDto(
                log.getId(),
                log.getTicket() != null ? log.getTicket().getId() : null,
                log.getActionType(),
                log.getActionDetails(),
                log.getCreatedAt()
        ));
    }

    private Role parseRole(String roleValue) {
        if (roleValue == null) {
            throw new BadRequestException("Role is required");
        }
        try {
            return Role.valueOf(roleValue.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Unknown role: " + roleValue);
        }
    }
}

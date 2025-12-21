package com.yazilimxyz.enterprise_ticket_system.service.user;

import com.yazilimxyz.enterprise_ticket_system.dto.user.UserListItemDto;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserListItemDto> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::mapToUserListItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserListItemDto> getActiveUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .filter(User::isActive)
                .map(this::mapToUserListItemDto)
                .collect(Collectors.toList());
    }

    private UserListItemDto mapToUserListItemDto(User user) {
        return UserListItemDto.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}

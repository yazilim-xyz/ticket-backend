package com.example.ticketing.service.impl;

import com.example.ticketing.dto.admin.AdminCreateTicketCommentRequest;
import com.example.ticketing.dto.admin.AdminTicketCommentDto;
import com.example.ticketing.entity.Ticket;
import com.example.ticketing.entity.TicketComment;
import com.example.ticketing.entity.User;
import com.example.ticketing.exception.BusinessException;
import com.example.ticketing.exception.ResourceNotFoundException;
import com.example.ticketing.mapper.AdminTicketCommentMapper;
import com.example.ticketing.repository.TicketCommentRepository;
import com.example.ticketing.repository.TicketRepository;
import com.example.ticketing.repository.UserRepository;
import com.example.ticketing.service.admin.AdminTicketCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminTicketCommentServiceImpl implements AdminTicketCommentService {

    private final TicketCommentRepository ticketCommentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AdminTicketCommentDto> getCommentsForTicket(Long ticketId) {
        ensureTicketExists(ticketId);
        return ticketCommentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(AdminTicketCommentMapper::toDto)
                .toList();
    }

    @Override
    public AdminTicketCommentDto addComment(AdminCreateTicketCommentRequest request) {
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + request.getTicketId()));
        User author = getCurrentUser()
                .orElseThrow(() -> new BusinessException("Authenticated user not found for comment creation"));

        TicketComment comment = TicketComment.builder()
                .ticket(ticket)
                .author(author)
                .content(request.getContent())
                .build();
        TicketComment saved = ticketCommentRepository.save(comment);
        return AdminTicketCommentMapper.toDto(saved);
    }

    private void ensureTicketExists(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException("Ticket not found with id " + ticketId);
        }
    }

    private java.util.Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return java.util.Optional.empty();
        }
        return userRepository.findByEmail(authentication.getName());
    }
}

package com.example.ticketing.service.impl;

import com.example.ticketing.dto.admin.AdminAiTicketSuggestionDto;
import com.example.ticketing.dto.admin.AdminTicketDto;
import com.example.ticketing.dto.admin.AdminUpdateAiSuggestionStatusRequest;
import com.example.ticketing.entity.AiSuggestionStatus;
import com.example.ticketing.entity.AiTicketSuggestion;
import com.example.ticketing.entity.Ticket;
import com.example.ticketing.entity.TicketPriority;
import com.example.ticketing.entity.TicketStatus;
import com.example.ticketing.entity.User;
import com.example.ticketing.exception.ResourceNotFoundException;
import com.example.ticketing.mapper.AdminAiSuggestionMapper;
import com.example.ticketing.mapper.AdminTicketMapper;
import com.example.ticketing.repository.AiTicketSuggestionRepository;
import com.example.ticketing.repository.TicketRepository;
import com.example.ticketing.repository.UserRepository;
import com.example.ticketing.service.admin.AdminAiSuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminAiSuggestionServiceImpl implements AdminAiSuggestionService {

    private final AiTicketSuggestionRepository aiTicketSuggestionRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AdminAiTicketSuggestionDto> getAll() {
        return aiTicketSuggestionRepository.findAll()
                .stream()
                .map(AdminAiSuggestionMapper::toDto)
                .toList();
    }

    @Override
    public AdminAiTicketSuggestionDto updateStatus(Long id, AdminUpdateAiSuggestionStatusRequest request) {
        AiTicketSuggestion suggestion = aiTicketSuggestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AI suggestion not found with id " + id));
        suggestion.setStatus(request.getStatus());
        AiTicketSuggestion saved = aiTicketSuggestionRepository.save(suggestion);
        return AdminAiSuggestionMapper.toDto(saved);
    }

    @Override
    public AdminTicketDto createTicketFromSuggestion(Long id, Long creatorUserId, Long assigneeUserId) {
        AiTicketSuggestion suggestion = aiTicketSuggestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AI suggestion not found with id " + id));
        User creator = userRepository.findById(creatorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + creatorUserId));
        User assignee = assigneeUserId != null ? userRepository.findById(assigneeUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + assigneeUserId)) : null;

        Ticket ticket = Ticket.builder()
                .title(suggestion.getSuggestedTitle())
                .description(suggestion.getSuggestedDescription())
                .priority(suggestion.getPriorityGuess() != null ? suggestion.getPriorityGuess() : TicketPriority.MEDIUM)
                .status(TicketStatus.OPEN)
                .createdBy(creator)
                .assignedTo(assignee)
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);
        suggestion.setStatus(AiSuggestionStatus.APPROVED);
        aiTicketSuggestionRepository.save(suggestion);

        return AdminTicketMapper.toDto(savedTicket);
    }
}

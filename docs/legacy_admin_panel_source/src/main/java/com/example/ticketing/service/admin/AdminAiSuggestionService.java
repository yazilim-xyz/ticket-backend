package com.example.ticketing.service.admin;

import com.example.ticketing.dto.admin.AdminAiTicketSuggestionDto;
import com.example.ticketing.dto.admin.AdminTicketDto;
import com.example.ticketing.dto.admin.AdminUpdateAiSuggestionStatusRequest;

import java.util.List;

public interface AdminAiSuggestionService {

    List<AdminAiTicketSuggestionDto> getAll();

    AdminAiTicketSuggestionDto updateStatus(Long id, AdminUpdateAiSuggestionStatusRequest request);

    AdminTicketDto createTicketFromSuggestion(Long id, Long creatorUserId, Long assigneeUserId);
}

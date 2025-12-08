package com.example.ticketing.mapper;

import com.example.ticketing.dto.admin.AdminAiTicketSuggestionDto;
import com.example.ticketing.entity.AiTicketSuggestion;
import com.example.ticketing.entity.User;

public final class AdminAiSuggestionMapper {

    private AdminAiSuggestionMapper() {
    }

    public static AdminAiTicketSuggestionDto toDto(AiTicketSuggestion suggestion) {
        if (suggestion == null) {
            return null;
        }
        User creator = suggestion.getCreatedBy();
        return AdminAiTicketSuggestionDto.builder()
                .id(suggestion.getId())
                .suggestedTitle(suggestion.getSuggestedTitle())
                .suggestedDescription(suggestion.getSuggestedDescription())
                .priorityGuess(suggestion.getPriorityGuess())
                .status(suggestion.getStatus())
                .createdById(creator != null ? creator.getId() : null)
                .createdByName(creator != null ? creator.getFullName() : null)
                .createdAt(suggestion.getCreatedAt())
                .build();
    }
}

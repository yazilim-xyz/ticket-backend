package com.example.ticketing.repository;

import com.example.ticketing.entity.AiTicketSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiTicketSuggestionRepository extends JpaRepository<AiTicketSuggestion, Long> {
}

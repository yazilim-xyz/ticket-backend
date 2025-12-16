package com.example.ticketing.repository;

import com.example.ticketing.entity.InternalChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InternalChatRepository extends JpaRepository<InternalChat, Long> {

    List<InternalChat> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
}

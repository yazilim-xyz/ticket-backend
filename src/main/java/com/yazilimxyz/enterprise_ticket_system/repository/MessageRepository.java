package com.yazilimxyz.enterprise_ticket_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yazilimxyz.enterprise_ticket_system.entities.InternalChat;

public interface MessageRepository extends JpaRepository<InternalChat, Long> {

    List<InternalChat> findBySenderAndReceiver(Long senderId, Long receiverId);
}

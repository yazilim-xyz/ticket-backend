package com.yazilimxyz.enterprise_ticket_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yazilimxyz.enterprise_ticket_system.entities.InternalChat;

public interface MessageRepository extends JpaRepository<InternalChat, Long> {

    // A is loggedInUser and getting messages could be both ways.
    // sender = A and receiver = B OR
    // sender = B and receiver = A
    List<InternalChat> findBySender_IdAndReceiver_IdOrSender_IdAndReceiver_Id(
            Long senderId1,
            Long receiverId1,
            Long senderId2,
            Long receiverId2);

}

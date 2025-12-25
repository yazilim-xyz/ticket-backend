package com.yazilimxyz.enterprise_ticket_system.controller.chat;

import java.security.Principal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.yazilimxyz.enterprise_ticket_system.dto.chat.ChatMessageResponseDto;
import com.yazilimxyz.enterprise_ticket_system.dto.chat.MessageDto;
import com.yazilimxyz.enterprise_ticket_system.entities.InternalChat;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.repository.MessageRepository;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;

import java.util.stream.Collectors;

@Controller
public class ChatController {
        @Autowired
        private SimpMessagingTemplate messagingTemplate;

        @Autowired
        private MessageRepository messageRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private SimpUserRegistry simpUserRegistry;

        @MessageMapping("/chat")
        public void processMessage(@Payload MessageDto messageDto, Principal principal) {

                InternalChat chatMessage = new InternalChat();
                long senderId = Long.parseLong(principal.getName());

                // Fetch sender and receiver from database
                User sender = userRepository.findById(senderId)
                                .orElseThrow(() -> new RuntimeException("Sender not found"));
                User receiver = userRepository.findById(messageDto.receiverId())
                                .orElseThrow(() -> new RuntimeException("Receiver not found"));

                chatMessage.setSender(sender);
                chatMessage.setReceiver(receiver);
                chatMessage.setMessage(messageDto.message());
                chatMessage.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

                // Save to database
                InternalChat savedMessage = messageRepository.save(chatMessage);

                // Convert to DTO for WebSocket response
                ChatMessageResponseDto responseDto = new ChatMessageResponseDto(
                                savedMessage.getId(),
                                sender.getId(),
                                sender.getName() + " " + sender.getSurname(),
                                receiver.getId(),
                                receiver.getName() + " " + receiver.getSurname(),
                                savedMessage.getMessage(),
                                savedMessage.getCreatedAt());

                // Send DTO to the receiver if they are connected
                messagingTemplate.convertAndSendToUser(
                                messageDto.receiverId().toString(),
                                "/queue/messages",
                                responseDto);
        }

        @GetMapping("api/messages/{otherUserId}")
        public ResponseEntity<List<ChatMessageResponseDto>> getChatHistory(@PathVariable Long otherUserId,
                        Principal principal) {
                long userId = Long.parseLong(principal.getName());

                List<InternalChat> messages = messageRepository.findBySender_IdAndReceiver_IdOrSender_IdAndReceiver_Id(
                                userId,
                                otherUserId, otherUserId, userId);
                messages.sort(Comparator.comparing(InternalChat::getCreatedAt));

                List<ChatMessageResponseDto> responseDtos = messages.stream()
                                .map(msg -> new ChatMessageResponseDto(
                                                msg.getId(),
                                                msg.getSender().getId(),
                                                msg.getSender().getName() + " " + msg.getSender().getSurname(),
                                                msg.getReceiver().getId(),
                                                msg.getReceiver().getName() + " " + msg.getReceiver().getSurname(),
                                                msg.getMessage(),
                                                msg.getCreatedAt()))
                                .collect(Collectors.toList());

                return ResponseEntity.ok(responseDtos);
        }
}

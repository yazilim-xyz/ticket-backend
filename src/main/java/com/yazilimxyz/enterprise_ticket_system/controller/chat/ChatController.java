package com.yazilimxyz.enterprise_ticket_system.controller.chat;

import java.security.Principal;
import java.time.LocalDateTime;
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
import com.yazilimxyz.enterprise_ticket_system.exception.UnauthorizedException;
import com.yazilimxyz.enterprise_ticket_system.repository.MessageRepository;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import com.yazilimxyz.enterprise_ticket_system.security.AuthenticatedUser;

import java.util.stream.Collectors;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

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

    // TODO kullanıcılar elle user id girmeyecek onun yerine çalışanları listeleyen bir endpoint lazım. getallusers gibi. 
    // oradan seçtiğine mesaj gidecek ve oradan seçtiğiyle olan geçmiş mesajları görecek. elle id girilemeyeceğinden böyle olması lazım

    @MessageMapping("/chat")
    public void processMessage(@Payload MessageDto messageDto, Principal principal) {

        InternalChat chatMessage = new InternalChat();
        AuthenticatedUser senderPrincipal = resolvePrincipal(principal);
        long senderId = senderPrincipal.id();

        // Fetch sender and receiver from database
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UnauthorizedException("Sender not found"));
        User receiver = userRepository.findById(messageDto.receiverId())
                .orElseThrow(() -> new UnauthorizedException("Receiver not found"));

        chatMessage.setSender(sender);
        chatMessage.setReceiver(receiver);
        chatMessage.setMessage(messageDto.message());
        chatMessage.setCreatedAt(LocalDateTime.now());

        // Save to database
        InternalChat savedMessage = messageRepository.save(chatMessage);

        // Convert to DTO for WebSocket response
        ChatMessageResponseDto responseDto = new ChatMessageResponseDto(
                savedMessage.getId(),
                sender.getId(),
                sender.getFullName(),
                receiver.getId(),
                receiver.getFullName(),
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
            @AuthenticationPrincipal AuthenticatedUser principal) {
        long userId = Optional.ofNullable(principal)
                .map(AuthenticatedUser::id)
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        List<InternalChat> messages = messageRepository.findBySender_IdAndReceiver_IdOrSender_IdAndReceiver_Id(userId,
                otherUserId, otherUserId, userId);
        messages.sort(Comparator.comparing(InternalChat::getCreatedAt));

        List<ChatMessageResponseDto> responseDtos = messages.stream()
                .map(msg -> new ChatMessageResponseDto(
                        msg.getId(),
                        msg.getSender().getId(),
                        msg.getSender().getFullName(),
                        msg.getReceiver().getId(),
                        msg.getReceiver().getFullName(),
                        msg.getMessage(),
                        msg.getCreatedAt()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }

    private AuthenticatedUser resolvePrincipal(Principal principal) {
        if (principal instanceof AuthenticatedUser user) {
            return user;
        }
        if (principal instanceof Authentication authentication) {
            Object innerPrincipal = authentication.getPrincipal();
            if (innerPrincipal instanceof AuthenticatedUser user) {
                return user;
            }
        }
        if (principal instanceof UsernamePasswordAuthenticationToken token
                && token.getPrincipal() instanceof AuthenticatedUser user) {
            return user;
        }
        throw new UnauthorizedException("Invalid authentication context");
    }
}

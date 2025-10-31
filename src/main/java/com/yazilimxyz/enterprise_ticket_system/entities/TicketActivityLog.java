package com.yazilimxyz.enterprise_ticket_system.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_activity_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TicketActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "action_type", length = 50, nullable = false)
    private String actionType;

    @Column(name = "action_details", columnDefinition = "text")
    private String actionDetails;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

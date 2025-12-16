package com.yazilimxyz.enterprise_ticket_system.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(length = 30)
    private String role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private UserPreference preference;

    @OneToMany(mappedBy = "assignedUser", fetch = FetchType.LAZY)
    private List<Ticket> assignedTickets;

    @OneToMany(mappedBy = "assignedByAdmin", fetch = FetchType.LAZY)
    private List<Ticket> adminAssignedTickets;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<TicketComment> comments;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<TicketNotification> notifications;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<TicketActivityLog> activityLogs;

    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    private List<InternalChat> sentMessages;

    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    private List<InternalChat> receivedMessages;
}

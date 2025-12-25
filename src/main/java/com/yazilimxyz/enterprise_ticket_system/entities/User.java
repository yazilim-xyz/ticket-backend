package com.yazilimxyz.enterprise_ticket_system.entities;

import java.time.OffsetDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "surname", nullable = false, length = 50)
    private String surname;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    // BCrypt hashed password
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    // ENUM Role: USER, ADMIN
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private Role role;

    @Column(name = "created_at")
    private OffsetDateTime createdAt; // user creation time

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt; // last update time

    @Column(name = "is_active", nullable = false)
    private boolean active = true; // account status flag (disable/login block)

    @Column(name = "is_approved", nullable = false)
    private boolean approved = false; // admin approval flag for new registrations

    // Ticket/chat relationships
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private UserPreference preference;

    @OneToMany(mappedBy = "assignedTo", fetch = FetchType.LAZY)
    private List<Ticket> assignedTickets;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<Ticket> createdTickets;

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
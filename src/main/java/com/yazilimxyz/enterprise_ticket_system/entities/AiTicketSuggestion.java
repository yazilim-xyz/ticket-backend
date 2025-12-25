package com.yazilimxyz.enterprise_ticket_system.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "ai_ticket_suggestions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiTicketSuggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(name = "suggestion_text", columnDefinition = "text", nullable = false)
    private String suggestionText;

    @Column(name = "confidence_score", precision = 4, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}

package com.yazilimxyz.enterprise_ticket_system.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResolutionStatsDTO {
    private Long userId;
    private String userName;
    private String userSurname;
    private String userEmail;
    
    // İstatistikler
    private Long resolvedCount;      // Çözülen ticket sayısı
    private Long unResolvedCount;    // Çözülmeyen ticket sayısı
    private Double successRate;      // Başarı yüzdesi (0-100)
    private Long averageResolutionTime;  // Ortalama çözme süresi (dakika)
}

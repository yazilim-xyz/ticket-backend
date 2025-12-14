package com.yazilimxyz.enterprise_ticket_system.controller;

import com.yazilimxyz.enterprise_ticket_system.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService ticketService;

    /**
     * Excel Reports ekranında kullanılacak endpoint.
     * Frontend bu endpoint'e GET isteği atar ve dönen bytes'ı
     * .xlsx dosyası olarak indirir.
     */
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportTicketsToExcel() {
        byte[] excelFile = ticketService.exportAllTicketsToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tickets.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelFile);
    }
}

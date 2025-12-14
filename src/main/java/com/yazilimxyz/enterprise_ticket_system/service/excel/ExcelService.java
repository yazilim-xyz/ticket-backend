package com.yazilimxyz.enterprise_ticket_system.service;

import com.yazilimxyz.enterprise_ticket_system.entities.Ticket;
import com.yazilimxyz.enterprise_ticket_system.repository.TicketRepository;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private final TicketRepository ticketRepository;

    /**
     * Excel Reports sayfasında kullanılacak olan,
     * tüm ticket kayıtlarını içeren Excel dosyasını üretir.
     */
    public byte[] exportAllTicketsToExcel() {
        List<Ticket> tickets = ticketRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Tickets");

            createHeaderRow(sheet);
            fillDataRows(sheet, tickets);

            // Kolon genişliklerini otomatik ayarla
            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating Excel", e);
        }
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Title", "Status", "Priority","Assigned To", "Created At"};

        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
    }

    private void fillDataRows(Sheet sheet, List<Ticket> tickets) {
        int rowIndex = 1;

        for (Ticket ticket : tickets) {
            Row row = sheet.createRow(rowIndex++);
            // ID as numeric (double) to keep Excel number formatting
            if (ticket.getId() != null) {
                row.createCell(0).setCellValue(ticket.getId().doubleValue());
            } else {
                row.createCell(0).setCellValue(0.0);
            }

            row.createCell(1).setCellValue(ticket.getTitle() != null ? ticket.getTitle() : "");
            row.createCell(2).setCellValue(ticket.getStatus() != null ? ticket.getStatus().name() : "");
            row.createCell(3).setCellValue(ticket.getPriority() != null ? ticket.getPriority().name() : "");
            row.createCell(4).setCellValue(ticket.getAssignedTo() != null ? (ticket.getAssignedTo().getEmail() != null ? ticket.getAssignedTo().getEmail() : ticket.getAssignedTo().getFullName()) : "");
            row.createCell(5).setCellValue(ticket.getCreatedAt() != null ? ticket.getCreatedAt().toString() : "");
        }
    }
}

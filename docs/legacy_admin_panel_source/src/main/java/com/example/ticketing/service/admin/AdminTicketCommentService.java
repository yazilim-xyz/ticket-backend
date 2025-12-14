package com.example.ticketing.service.admin;

import com.example.ticketing.dto.admin.AdminCreateTicketCommentRequest;
import com.example.ticketing.dto.admin.AdminTicketCommentDto;

import java.util.List;

public interface AdminTicketCommentService {

    List<AdminTicketCommentDto> getCommentsForTicket(Long ticketId);

    AdminTicketCommentDto addComment(AdminCreateTicketCommentRequest request);
}

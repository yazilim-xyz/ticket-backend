package com.yazilimxyz.enterprise_ticket_system.dto.ticket;

public class TicketDto {
    private Long id;
    private String title;
    private String description;
    private Long createdById;
    private String createdByName;
    private Long assignedToId;
    private String assignedToName;

    public TicketDto(Long id, String title, String description, Long createdById, 
                     String createdByName, Long assignedToId, String assignedToName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdById = createdById;
        this.createdByName = createdByName;
        this.assignedToId = assignedToId;
        this.assignedToName = assignedToName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public Long getAssignedToId() {
        return assignedToId;
    }

    public void setAssignedToId(Long assignedToId) {
        this.assignedToId = assignedToId;
    }

    public String getAssignedToName() {
        return assignedToName;
    }

    public void setAssignedToName(String assignedToName) {
        this.assignedToName = assignedToName;
    }
}

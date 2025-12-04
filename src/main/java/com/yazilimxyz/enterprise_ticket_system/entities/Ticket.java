import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketCategory;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketPriority;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketStatus;
import jakarta.persistence.*;
import java.time.OffsetDateTime;



@Entity
@Table(name="Tickets")
public class Ticket {
 //ID
    @Id
    @GeneretedValue(strategy= GenerationType.IDENTITY)
    private Long id;
//colums
    @Column(nullable=false ,lenght 255)
    private String tittle;
    @Column(nullable=false,columnDefination="TEXT")
    private String description;
//enums
    @Enumerated(EnumType.STRİNG)
    @Column(nullable=false,lenght 50)
    private TicketStatus status= TicketStatus.OPEN;
    @Enumrated(EnumType.STRİNG)
    @Column(nullable=false,lenght 50)
    private TicketPriority priority=TicketPriority.MEDİUM;
    @Enumrated(EnumType.STRİNG)
    @Column(nullable=false,lenght 50)
    private TicketCategory category = TicketCatagory.OTHER;

//user kısmı
    //kullanıcıyla eslestiriyoruz.Id ler ile olusturma ve assignedleri
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="created_by_id")
    private User createdBy;
    @ManyToOne(fetch=FetchType.Lazy)
    @JoinColumn(name="assigned_to_id")
    private User assignedBy;
//eklenebilir
    private OffsetDateTime dueDate;
    @Column(columnDefinition="TEXT")
    private String resolutionSummary;
    @Column(nullable=false)
    private Boolean isDeleted = false;
    @Column(nullable=false)
    private OffSetDateTime createdAt= OffSetDateTime.now();
    @Column(nullable=false)
    private OffSetDateTime updateAt= OffSetDateTime.now();
    @Column(columnDefinition = "TEXT")
    private String rerolutionSummary;
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketComment> comments = new ArrayList<>();



    @PreUpdate
    public void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public Ticket() {
    }

    public String getresolutionSummary() {
        return resolutionSummary;
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

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    public TicketCategory getCategory() {
        return category;
    }

    public void setCategory(TicketCategory category) {
        this.category = category;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;//deneme
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public OffsetDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(OffsetDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getResolutionSummary() {
        return resolutionSummary;
    }

    public void setResolutionSummary(String resolutionSummary) {
        this.resolutionSummary = resolutionSummary;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }



}
package com.project.societyManagement.entity;


import com.project.societyManagement.entity.common.AuditableEntity;
import com.project.societyManagement.entity.types.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "facility_booking",
        uniqueConstraints = @UniqueConstraint(columnNames = {"facility_id", "user_id", "status"})
)
public class FacilityBooking extends AuditableEntity {
    @ManyToOne
    @JoinColumn(name = "facility_id",nullable = false)
    private Facility facility;
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    @Column(name = "admin_comment")
    private String adminComments;
    @PrePersist
    public void prePersist() {
        if (status == null) status = BookingStatus.PENDING;
    }

}

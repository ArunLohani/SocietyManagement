package com.project.societyManagement.entity;


import com.project.societyManagement.entity.common.AuditableEntity;
import com.project.societyManagement.entity.types.ParkingRequestStatus;
import com.project.societyManagement.entity.types.ParkingSlotStatus;
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
@Table(name = "parking_requests")
public class    ParkingRequest extends AuditableEntity {

//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;

    @ManyToOne
    @JoinColumn(name = "flat_id")
    private Flat flat;

    @ManyToOne
    @JoinColumn(name = "requested_slot_id")
    private ParkingSlot requestedSlot;
    @Enumerated(EnumType.STRING)
    private ParkingRequestStatus status;
    private String adminComment;

    @PrePersist
    public void prePersist() {
        if (status == null) status = ParkingRequestStatus.PENDING;
    }
}

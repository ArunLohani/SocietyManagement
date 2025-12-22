package com.project.societyManagement.entity;

import com.project.societyManagement.entity.common.AuditableEntity;
import com.project.societyManagement.entity.types.BookingStatus;
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
@Table(name = "parking_slot")
public class ParkingSlot extends AuditableEntity {
    private String area;
    @Column(name = "slot_number")
    private String slotNumber;
    @Enumerated(EnumType.STRING)
    private ParkingSlotStatus status = ParkingSlotStatus.AVAILABLE;
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
    @ManyToOne
    @JoinColumn(name = "flat_id")
    private Flat flat;
    @ManyToOne
    @JoinColumn(name = "tenant_id",nullable = false)
    private Tenant tenant;

    @PrePersist
    public void prePersist() {
        if (status == null) status = ParkingSlotStatus.AVAILABLE;
    }

}

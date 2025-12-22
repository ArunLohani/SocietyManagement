package com.project.societyManagement.queryBuilder.facility;

import com.project.societyManagement.entity.FacilityRegisteredUser;
import com.project.societyManagement.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityFilter {
    private Long id;
    private String facilityName;
    private Integer capacity;
    private String description;
    private Long manager;
    private String location;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Boolean openForAll;
    private Long tenantId;
    private Boolean isActive = true;
}

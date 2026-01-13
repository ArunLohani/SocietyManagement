package com.project.societyManagement.dto.Facility;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityCreationRequest {
    private String facilityName;
    private Integer capacity;
    private String description;
    private Long manager;
    private String location;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Boolean openForAll;

}

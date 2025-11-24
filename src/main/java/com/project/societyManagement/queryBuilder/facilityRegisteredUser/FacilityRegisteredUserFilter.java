package com.project.societyManagement.queryBuilder.facilityRegisteredUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityRegisteredUserFilter {
    private Long id;
    private Long facility;
    private Long user;
}

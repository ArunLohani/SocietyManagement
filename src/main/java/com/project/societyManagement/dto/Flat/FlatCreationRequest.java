package com.project.societyManagement.dto.Flat;

import com.project.societyManagement.entity.types.FlatCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlatCreationRequest {
    private String block;
    private Integer number;
    private FlatCategory category;
    private Integer floor;
    private Integer sqFt;
}

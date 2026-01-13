package com.project.societyManagement.dto.Flat;

import com.project.societyManagement.entity.types.FlatCategory;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlatCreationRequest {
    @NotEmpty(message = "Block cannot be empty")
    private String block;
    @NotEmpty(message = "Flat Number cannot be empty")
    private Integer number;
    @NotEmpty(message = "Flat Category cannot be empty")
    private FlatCategory category;
    @NotEmpty(message = "Floor cannot be empty")
    private Integer floor;
    @NotEmpty(message = "Flat size cannot be empty")
    private Integer sqFt;
}

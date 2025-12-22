package com.project.societyManagement.queryBuilder.flat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlatFilter {
    private Long id;
    private Boolean isActive = true;
    private Long tenant;
    private String block;
    private Integer number;
    private Long member;
    private String category;
    private Integer floor;
    private Integer sqFt;
}

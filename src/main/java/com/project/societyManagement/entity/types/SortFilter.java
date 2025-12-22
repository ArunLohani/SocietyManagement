package com.project.societyManagement.entity.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SortFilter {
    private String property;
    private Boolean asc = true;
}

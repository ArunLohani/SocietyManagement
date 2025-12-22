package com.project.societyManagement.queryBuilder.notice;

import com.project.societyManagement.entity.types.SortFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeFilter {
    private Long id;
    private String title;
    private String message;
    private String category;
    private Boolean isExpired;
    private Boolean isPublic;
    private Boolean isActive = true;
    private Long tenantId;
    private SortFilter sortFilter = new SortFilter("createdAt",false);
}

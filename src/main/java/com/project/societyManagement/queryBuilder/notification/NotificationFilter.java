package com.project.societyManagement.queryBuilder.notification;

import com.project.societyManagement.entity.types.NotificationType;
import com.project.societyManagement.entity.types.SortFilter;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationFilter {

    private Long id;
    private String title;
    private String message;
    private String url;
    private String type;
    private Long userId;
    private Long societyId;
    private Boolean read;
    private Boolean isActive = true;
    private LocalDate createdAt = LocalDate.now();
    private SortFilter sortFilter = new SortFilter("createdAt",false);
}

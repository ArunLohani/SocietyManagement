package com.project.societyManagement.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeCreatedEvent {
    private Long noticeId;
    private String title;
    private Long tenantId;
}

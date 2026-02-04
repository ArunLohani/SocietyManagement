package com.project.societyManagement.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogEvent {
    String entity;
    String action;
    Long entityId;
    String source;
    String method;
    Long performedBy;

}

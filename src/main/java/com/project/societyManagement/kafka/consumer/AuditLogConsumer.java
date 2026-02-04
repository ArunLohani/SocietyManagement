package com.project.societyManagement.kafka.consumer;

import com.project.societyManagement.entity.AuditLogs;
import com.project.societyManagement.kafka.dto.AuditLogEvent;
import com.project.societyManagement.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuditLogConsumer {

    private final AuditLogRepository auditLogRepository;

    @KafkaListener(
            topics = "audit-log-event",
            groupId = "society-group"
    )
    public void handleAuditLog(AuditLogEvent auditLog){

        AuditLogs auditLogs = AuditLogs.builder()
                .action(auditLog.getAction())
                .source(auditLog.getSource())
                .entityId(auditLog.getEntityId())
                .method(auditLog.getMethod())
                .performedBy(auditLog.getPerformedBy())
                .entity(auditLog.getEntity())
                .build();
            auditLogRepository.save(auditLogs);
    }

}

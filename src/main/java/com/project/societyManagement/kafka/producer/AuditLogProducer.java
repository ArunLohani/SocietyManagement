package com.project.societyManagement.kafka.producer;

import com.project.societyManagement.kafka.dto.AuditLogEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuditLogProducer {
    private final KafkaTemplate<String, AuditLogEvent> kafkaTemplate;

    public void publishAuditLog(AuditLogEvent auditLog){
        this.kafkaTemplate.send("audit-log-event",auditLog);
    }

}

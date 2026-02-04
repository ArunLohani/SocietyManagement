package com.project.societyManagement.kafka.consumer;

import com.project.societyManagement.entity.ImpersonationAuditLog;
import com.project.societyManagement.kafka.dto.ImpersonationLogEvent;
import com.project.societyManagement.repository.ImpersonationAuditLogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImpersonationLogConsumer {

    private final ImpersonationAuditLogsRepository impersonationAuditLogsRepository;

    @KafkaListener(
            topics = "impersonation-log",
            groupId = "society-group"
    )
    public void handleImpersonationLogEvent(ImpersonationLogEvent impersonationLogEvent){

        ImpersonationAuditLog impersonationAuditLog = ImpersonationAuditLog.builder()
                .endedAt(impersonationLogEvent.getEndedAt())
                .startedAt(impersonationLogEvent.getStartedAt())
                .reason(impersonationLogEvent.getReason())
                .action(impersonationLogEvent.getAction())
                .adminId(impersonationLogEvent.getAdminId())
                .superAdminId(impersonationLogEvent.getSuperAdminId())
                .sessionId(impersonationLogEvent.getSessionId())
                .ticketId(impersonationLogEvent.getTicketId())
                .build();

        impersonationAuditLogsRepository.save(impersonationAuditLog);
    }
}

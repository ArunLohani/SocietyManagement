package com.project.societyManagement.aspect;

import com.project.societyManagement.annotations.ImpersonationAuditing;
import com.project.societyManagement.entity.ImpersonationSession;
import com.project.societyManagement.entity.types.ImpersonationAction;
import com.project.societyManagement.kafka.dto.ImpersonationLogEvent;
import com.project.societyManagement.kafka.producer.ImpersonationLogProducer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class ImpersonationAuditAspect {

    private final ImpersonationLogProducer impersonationLogProducer;

    @AfterReturning(
            value = "@annotation(impersonationAuditing)",
            returning = "result"
    )
    public void impersonationAuditAfterSuccess(
            JoinPoint joinPoint,
            ImpersonationAuditing impersonationAuditing,
            Object result
    ) {

        if (!(result instanceof ImpersonationSession session)) {
            return; // safety
        }

        LocalDateTime startedAt = null;
        LocalDateTime endedAt = null;

        if (impersonationAuditing.action().equals(ImpersonationAction.START) ) {
            startedAt = LocalDateTime.now();
        } else {
            endedAt = LocalDateTime.now();
        }

        ImpersonationLogEvent event = ImpersonationLogEvent.builder()
                .sessionId(session.getId())
                .ticketId(session.getTicket().getId())
                .superAdminId(session.getSuperAdmin().getId())
                .adminId(session.getAdmin().getId())
                .action(impersonationAuditing.action().name())
                .reason(impersonationAuditing.reason())
                .startedAt(startedAt)
                .endedAt(endedAt)
                .build();

        impersonationLogProducer.publishImpersonationLog(event);
    }

}

package com.project.societyManagement.aspect;

import com.project.societyManagement.annotations.Auditing;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.common.AuditableEntity;
import com.project.societyManagement.kafka.dto.AuditLogEvent;
import com.project.societyManagement.kafka.producer.AuditLogProducer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogProducer auditLogProducer;

    @AfterReturning(
            value = "@annotation(auditing)",
            returning = "result"
    )
    public void auditAfterSuccess(
            JoinPoint joinPoint,
            Auditing auditing,
            Object result
    ) {

        Long entityId = extractEntityId(result);
        Long userId = extractUserId();
        String source = resolveSource();

        AuditLogEvent event = AuditLogEvent.builder()
                .entity(auditing.entity())
                .action(auditing.action())
                .entityId(entityId)
                .method(joinPoint.getSignature().toShortString())
                .source(source)
                .performedBy(userId)
                .build();

        auditLogProducer.publishAuditLog(event);
    }

    private Long extractEntityId(Object result) {
        if (result instanceof AuditableEntity auditableEntity) {
            return auditableEntity.getId();
        }
        return null;
    }

    private Long extractUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null &&
                authentication.getPrincipal() instanceof User user) {
            return user.getId();
        }

        return null;
    }


    private String resolveSource() {

        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs == null) {
            return "SYSTEM";
        }

        HttpServletRequest request = attrs.getRequest();

        String uri = request.getRequestURI();

        if (uri != null && uri.contains("/graphql")) {
            return "GRAPHQL";
        }

        return "REST";
    }
}

package com.project.societyManagement.dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetails {
    private Long id;
    private String name;
    private String email;
    private Set<String> roles;
    private Long tenantId;
    private String societyName;

    // Impersonation information
    private Long sessionId;
    private Boolean isImpersonating;
    private Long impersonationSessionId;
    private String superAdminEmail;
    private LocalDateTime impersonationExpiresAt;
}
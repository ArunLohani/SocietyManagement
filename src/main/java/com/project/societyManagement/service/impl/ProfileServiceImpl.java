package com.project.societyManagement.service.impl;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.User.UserDetails;
import com.project.societyManagement.entity.ImpersonationSession;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.config.ImpersonationAuthenticationToken;
import com.project.societyManagement.service.ImpersonationSessionService;
import com.project.societyManagement.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ImpersonationSessionService impersonationSessionService;

    @Override
    public ApiResponse<UserDetails> getMyProfile(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        boolean isImpersonating = false;
        Long sessionId = null;
        String superAdminEmail = null;

        // ✅ Check impersonation using Authentication itself
        if (authentication instanceof ImpersonationAuthenticationToken token
                && token.isImpersonating()) {

            isImpersonating = true;
            sessionId = token.getSessionId();
            superAdminEmail = token.getSuperAdminEmail();
        }

        UserDetails.UserDetailsBuilder builder = UserDetails.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .sessionId(sessionId)
                .tenantId(user.getTenant() != null ? user.getTenant().getId() : null)
                .societyName(user.getTenant() != null ? user.getTenant().getName() : null)
                .roles(user.getRoles().stream()
                        .map(role -> role.getRole())
                        .collect(Collectors.toSet()))
                .isImpersonating(isImpersonating);

        // ✅ Add impersonation details safely
        if (isImpersonating && sessionId != null) {

            builder.impersonationSessionId(sessionId)
                    .superAdminEmail(superAdminEmail);

            try {
                ImpersonationSession session =
                        impersonationSessionService.findSessionById(sessionId);

                builder.impersonationExpiresAt(session.getExpiresAt());
            } catch (Exception ignored) {
                // Session missing or expired — profile still valid
            }
        }

        UserDetails userDetails = builder.build();

        return new ApiResponse<>(
                true,
                "Your Profile has been fetched successfully",
                userDetails
        );
    }
}

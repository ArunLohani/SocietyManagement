package com.project.societyManagement.service.impl;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.User.UserDetails;
import com.project.societyManagement.entity.ImpersonationSession;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.config.ImpersonationAuthenticationToken;
import com.project.societyManagement.service.ImpersonationSessionService;
import com.project.societyManagement.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final ImpersonationSessionService impersonationSessionService;

    @Override
    @Cacheable(
            value = "userProfile",
            key = "#authentication.principal.id + '_' + #authentication.principal.email",
            unless = "#result == null || #result.data == null"
    )
    public ApiResponse<UserDetails> getMyProfile(Authentication authentication) {
        log.info("🔥 getMyProfile() HIT – DB/logic executed for user: {}",
                ((User) authentication.getPrincipal()).getEmail());

        User user = (User) authentication.getPrincipal();

        boolean isImpersonating = false;
        Long sessionId = null;
        String superAdminEmail = null;

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

    // Add method to evict cache when user profile is updated
    @CacheEvict(
            value = "userProfile",
            key = "#userId + '_' + #userEmail"
    )
    public void evictUserProfileCache(Long userId, String userEmail) {
        log.info("🗑️ Evicting cache for user: {}", userEmail);
    }
}
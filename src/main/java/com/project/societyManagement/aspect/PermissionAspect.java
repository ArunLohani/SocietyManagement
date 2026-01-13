package com.project.societyManagement.aspect;

import com.project.societyManagement.annotations.RequiresPermission;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final PermissionService permissionService;

    @Before("@annotation(requiresPermission)")
    public void checkPermission(RequiresPermission requiresPermission) {
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        boolean allowed = permissionService.hasPermission(
                (User) authentication.getPrincipal(),
                requiresPermission.api()
        );
        if (!allowed) {
            throw new AccessDeniedException("You do not have permission to perform this action.");
        }
    }
}

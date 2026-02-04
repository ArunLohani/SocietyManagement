package com.project.societyManagement.service;

import com.project.societyManagement.entity.ImpersonationSession;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.queryBuilder.impersonationSession.ImpersonationSessionFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface ImpersonationSessionService {
    public ImpersonationSession findSessionById(Long sessionId);
    public ImpersonationSession createImpersonationSession(Long ticketId, HttpServletResponse response);
    public String generateImpersonationToken(ImpersonationSession session) ;
    @Transactional
    public ImpersonationSession endImpersonationSession(Long sessionId, User user);
    public boolean isSessionActive(Long sessionId) ;
    public List<ImpersonationSession> getSessions(ImpersonationSessionFilter filter);
    public boolean isSessionActiveWithoutAuth(Long sessionId);
    public Page<ImpersonationSession> getSessionsPaginated(
            ImpersonationSessionFilter filter, Pageable pageable);
}

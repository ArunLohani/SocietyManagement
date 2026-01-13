package com.project.societyManagement.repository;

import com.project.societyManagement.entity.ImpersonationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ImpersonationSessionRepo extends JpaRepository<ImpersonationSession,Long> {
    @Query("""
        SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
        FROM ImpersonationSession s
        WHERE s.id = :sessionId
        AND s.isActive = true
        AND s.endedAt IS NULL
        AND s.expiresAt > :currentTime
    """)
    boolean isSessionActive(@Param("sessionId") Long sessionId,
                            @Param("currentTime") LocalDateTime currentTime);

    /**
     * Find by ID with all relationships eagerly loaded
     */
    @Query("""
        SELECT s FROM ImpersonationSession s
        LEFT JOIN FETCH s.admin
        LEFT JOIN FETCH s.superAdmin
        LEFT JOIN FETCH s.ticket
        WHERE s.id = :id
    """)
    Optional<ImpersonationSession> findByIdWithRelations(@Param("id") Long id);

}

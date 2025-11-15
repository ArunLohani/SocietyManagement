package com.project.societyManagement.dto.Event;

import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCreationRequest {
    private String name;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String location;
    private EventStatus status = EventStatus.PUBLISHED;
    private Long organizedBy;
    private Boolean registrationRequired;
    private Integer maxParticipants;
}

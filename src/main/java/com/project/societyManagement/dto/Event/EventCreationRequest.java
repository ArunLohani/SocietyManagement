package com.project.societyManagement.dto.Event;

import com.project.societyManagement.entity.types.EventStatus;
import jakarta.validation.constraints.NotEmpty;
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
    @NotEmpty(message = "Name cannot be blank.")
    private String name;
    @NotEmpty(message = "Description cannot be blank.")
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    @NotEmpty(message = "Location cannot be blank.")
    private String location;
    private EventStatus status = EventStatus.PUBLISHED;
    @NotEmpty(message = "Organized By cannot be blank.")
    private Long organizedBy;
    @NotEmpty(message = "Registration Required cannot be blank.")
    private Boolean registrationRequired;
    private Integer maxParticipants;
}

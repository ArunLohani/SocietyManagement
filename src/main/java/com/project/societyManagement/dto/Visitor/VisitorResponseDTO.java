package com.project.societyManagement.dto.Visitor;

import com.project.societyManagement.entity.Flat;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.VisitorStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorResponseDTO {

    private Long id;
    private String visitorName;
    private String visitorPhone;
    private String visitorEmail;
    private String purpose;
    private String rejectionReason;
    // Visit timing
    private LocalDateTime expectedIn;
    private LocalDateTime expectedOut;
    private VisitorStatus status;
    private Flat flat;
    private User requestedBy; // resident or security (for walk-in)

}

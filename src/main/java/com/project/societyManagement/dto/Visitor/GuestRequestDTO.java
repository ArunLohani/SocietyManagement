package com.project.societyManagement.dto.Visitor;

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
public class GuestRequestDTO {

    @Column(nullable = false)
    private String visitorName;
    @Column(nullable = false)
    private String visitorPhone;
    @Column(nullable = false)
    private String visitorEmail;
    private String purpose;
    // Visit timing
    private LocalDateTime expectedIn;
    private LocalDateTime expectedOut;
    // Relationships
    private Long flat;

}

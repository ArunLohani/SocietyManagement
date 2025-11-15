package com.project.societyManagement.entity;

import com.project.societyManagement.entity.common.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "api_permission_mapping")
public class ApiPermissionMapping extends AuditableEntity {
   private Long id;
    @NotNull
    private String api;

    @ManyToOne
    @JoinColumn(name = "action_id")
   private Action action;
}

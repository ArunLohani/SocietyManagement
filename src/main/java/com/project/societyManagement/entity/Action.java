package com.project.societyManagement.entity;


import com.project.societyManagement.entity.common.AuditableEntity;
import com.project.societyManagement.entity.types.Actions;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "actions")
public class Action extends AuditableEntity {
    @Enumerated(EnumType.STRING)
    private Actions action;
}

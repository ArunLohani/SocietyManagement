package com.project.societyManagement.entity;

import com.project.societyManagement.entity.common.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "menu")
public class Menu extends AuditableEntity {

    @NotEmpty(message = "Menu Name cannot be empty.")
    @Column(name = "name")
    private String menuName;

    @NotEmpty(message = "Menu Description cannot be empty.")
    @Column(name = "description")
    private String menuDescription;




}

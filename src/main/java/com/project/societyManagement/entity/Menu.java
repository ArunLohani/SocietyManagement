package com.project.societyManagement.entity;

import com.project.societyManagement.entity.common.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "menu")
public class Menu extends AuditableEntity {

    @Column(name = "name")
    private String menuName;

    @Column(name = "description")
    private String menuDescription;


}

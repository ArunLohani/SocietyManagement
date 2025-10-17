package com.project.societyManagement.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.societyManagement.entity.common.AuditableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Tenant")
public class Tenant extends AuditableEntity {

    @NotNull
    private String name;

    @OneToMany(mappedBy = "tenant" , cascade = CascadeType.ALL)
    @JsonIgnore
    List<User> residents;

}

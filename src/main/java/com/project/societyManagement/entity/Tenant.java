package com.project.societyManagement.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.societyManagement.entity.common.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringExclude;

import java.util.ArrayList;
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

    @OneToMany(mappedBy = "tenant" , cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    List<User> residents = new ArrayList<>();

    @OneToMany(mappedBy = "tenant" , cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Event> events = new ArrayList<>();

    @OneToMany(mappedBy = "tenant" , cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Notice> notices = new ArrayList<>();

    @OneToMany(mappedBy = "tenant" , cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Notice> parkingSlots = new ArrayList<>();

    @OneToMany(mappedBy = "tenant" , cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Facility> facilities = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Flat> flats = new ArrayList<>();


}

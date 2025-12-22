package com.project.societyManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.societyManagement.entity.common.AuditableEntity;
import com.project.societyManagement.entity.types.FlatMembershipType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "flat_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_flat_user",
                        columnNames = {"flat_id", "user_id"}
                )
        })
public class FlatMember extends AuditableEntity {
    @ManyToOne
    @JoinColumn(name = "flat_id")
    @JsonIgnore
    private Flat flat;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    private FlatMembershipType type;
}

package com.project.societyManagement.entity;

import com.project.societyManagement.common.AuditableEntity;
import com.project.societyManagement.entity.type.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@Entity
@Table(name = "users")
public class User extends AuditableEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false,unique = true)
    private String email;
    private String password;
    @Column(name = "phone_number")
    @Size(max = 15 , min = 10)
    private Integer phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;








}

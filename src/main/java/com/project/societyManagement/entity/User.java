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


    private String name;

    @Column(nullable = false,unique = true)
    private String email;
    private String password;
    @Column(length = 10 , name = "phone_number")
    private Integer phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;








}

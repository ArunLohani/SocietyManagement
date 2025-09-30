package com.project.societyManagement.entity;

import com.project.societyManagement.common.AuditableEntity;
import com.project.societyManagement.entity.type.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User extends AuditableEntity {

    private String name;

    private String email;
    private String password;
    private Integer phoneNumber;
    private Role role;








}

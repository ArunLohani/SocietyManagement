package com.project.societyManagement.entity;

import com.project.societyManagement.common.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.project.societyManagement.entity.Role;
import java.util.Collection;
import java.util.List;
import java.util.Set;


@Data
@Entity
@Table(name = "users")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User extends AuditableEntity implements UserDetails {

    @NotEmpty(message = "Name cannot be blank.")
    @Size(min=6,message = "Name must be of atleast 6 characters long.")
    private String name;

    @NotEmpty
    @Email(message = "Email should be valid.")
    private String email;

    @NotEmpty(message = "Password cannot be blank.")
    @Size(min=8,message = "Password must be of atleast 8 characters long.")
    private String password;

    @Column(name = "phone_number")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }
}

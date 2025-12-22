package com.project.societyManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.societyManagement.entity.common.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;


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

    @Size(min=8,message = "Password must be of atleast 8 characters long.")
    private String password;

    @Column(name = "phone_number")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "tenant_id",nullable = false)
    private Tenant tenant;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole())).collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @ManyToMany(mappedBy = "participants",fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Event> eventsParticipated = new HashSet<>();

    @OneToMany(mappedBy = "raisedByUser", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Complaints> complaintsRaised = new ArrayList<>();

    @OneToMany(mappedBy = "assignedToUser", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Complaints> complaintsAssigned = new ArrayList<>();

//    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
//    @JsonIgnore
//    private List<Vehicle> vehicles = new ArrayList<>();

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
//    @JsonIgnore
//    private List<ParkingSlot> parkingSlots = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<FacilityRegisteredUser> registeredFacilities = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<FacilityBooking> facilityBookings = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<FlatMember> flatMemberships = new ArrayList<>();

}

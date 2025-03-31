package com.example.soeiapi.entities;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled = false;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Transient
    private Integer level;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false, orphanRemoval = true)
    @JsonIgnore // 🔹 Prevent infinite recursion
    private UserProfileEntity profile;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyEntity company;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = true, referencedColumnName = "user_id")
    private UserEntity parent;

    // @ManyToOne
    // @JoinColumn(name = "role_id", nullable = false)
    // private RoleEntity role;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_permissions", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<PermissionEntity> permissions = new HashSet<>();

    // @OneToMany(mappedBy = "user")
    // private Set<UserPermissionsEntity> userPermissions;

    @Column(name = "last_password_change_at")
    private Instant lastPasswordChangeAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        // Add roles as authorities
        authorities.addAll(roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .collect(Collectors.toSet()));

        // Add permissions as authorities
        authorities.addAll(permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermissionName()))
                .collect(Collectors.toSet()));

        // Log authorities for debugging
        authorities.forEach(authority -> System.out.println("Authority: " + authority.getAuthority()));
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @PostLoad
    public void calculateLevel() {
        this.level = computeUserLevel(this);
    }

    private int computeUserLevel(UserEntity user) {
        int level = 1;
        while (user.getParent() != null) {
            user = user.getParent();
            level++;
        }
        return level;
    }

    // set profile and ensure bidirectional mapping
    // public void setProfile(UserProfileEntity profile) {
    // this.profile = profile;
    // profile.setUser(this); // Ensure bidirectional mapping
    // }

}

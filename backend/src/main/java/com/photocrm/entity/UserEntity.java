package com.photocrm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "email"}))
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @NotBlank
    @Email
    @Size(max = 255)
    @Column(name = "email", nullable = false)
    private String email;

    @NotBlank
    @Size(max = 255)
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotBlank
    @Size(max = 100)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_permissions", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "permission")
    private Set<Permission> permissions = new HashSet<>();

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Constructors
    public UserEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public UserEntity(UUID tenantId, String email, String passwordHash, String firstName, String lastName, UserRole role) {
        this();
        this.tenantId = tenantId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.permissions = role.getDefaultPermissions();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    // Enums
    public enum UserRole {
        ADMIN("Administrator"),
        STAFF("Staff Member"),
        CLIENT("Client");

        private final String displayName;

        UserRole(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Set<Permission> getDefaultPermissions() {
            Set<Permission> permissions = new HashSet<>();
            switch (this) {
                case ADMIN:
                    permissions.addAll(Set.of(
                        Permission.ORDER_CREATE, Permission.ORDER_READ, Permission.ORDER_UPDATE, Permission.ORDER_DELETE,
                        Permission.PHOTO_UPLOAD, Permission.PHOTO_READ, Permission.PHOTO_UPDATE, Permission.PHOTO_DELETE,
                        Permission.INVOICE_CREATE, Permission.INVOICE_READ, Permission.INVOICE_UPDATE, Permission.INVOICE_DELETE,
                        Permission.PAYMENT_PROCESS, Permission.PAYMENT_READ, Permission.PAYMENT_REFUND,
                        Permission.USER_CREATE, Permission.USER_READ, Permission.USER_UPDATE, Permission.USER_DELETE,
                        Permission.TENANT_READ, Permission.TENANT_UPDATE,
                        Permission.ANALYTICS_READ
                    ));
                    break;
                case STAFF:
                    permissions.addAll(Set.of(
                        Permission.ORDER_CREATE, Permission.ORDER_READ, Permission.ORDER_UPDATE,
                        Permission.PHOTO_UPLOAD, Permission.PHOTO_READ, Permission.PHOTO_UPDATE,
                        Permission.INVOICE_READ,
                        Permission.USER_READ
                    ));
                    break;
                case CLIENT:
                    permissions.addAll(Set.of(
                        Permission.PHOTO_READ,
                        Permission.PAYMENT_PROCESS
                    ));
                    break;
            }
            return permissions;
        }
    }

    public enum Permission {
        // Order permissions
        ORDER_CREATE("order:create"),
        ORDER_READ("order:read"),
        ORDER_UPDATE("order:update"),
        ORDER_DELETE("order:delete"),

        // Photo permissions
        PHOTO_UPLOAD("photo:upload"),
        PHOTO_READ("photo:read"),
        PHOTO_UPDATE("photo:update"),
        PHOTO_DELETE("photo:delete"),

        // Invoice permissions
        INVOICE_CREATE("invoice:create"),
        INVOICE_READ("invoice:read"),
        INVOICE_UPDATE("invoice:update"),
        INVOICE_DELETE("invoice:delete"),

        // Payment permissions
        PAYMENT_PROCESS("payment:process"),
        PAYMENT_READ("payment:read"),
        PAYMENT_REFUND("payment:refund"),

        // User permissions
        USER_CREATE("user:create"),
        USER_READ("user:read"),
        USER_UPDATE("user:update"),
        USER_DELETE("user:delete"),

        // Tenant permissions
        TENANT_READ("tenant:read"),
        TENANT_UPDATE("tenant:update"),

        // Analytics permissions
        ANALYTICS_READ("analytics:read");

        private final String permission;

        Permission(String permission) {
            this.permission = permission;
        }

        public String getPermission() {
            return permission;
        }
    }
}
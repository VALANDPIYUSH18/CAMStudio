package com.photocrm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenants", 
       uniqueConstraints = @UniqueConstraint(columnNames = "subdomain"))
public class TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Size(max = 100)
    @Column(name = "subdomain", nullable = false, unique = true)
    private String subdomain;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false)
    private SubscriptionPlan plan;

    @Column(name = "settings", columnDefinition = "jsonb")
    private String settings;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Constructors
    public TenantEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public TenantEntity(String name, String subdomain, SubscriptionPlan plan) {
        this();
        this.name = name;
        this.subdomain = subdomain;
        this.plan = plan;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public SubscriptionPlan getPlan() {
        return plan;
    }

    public void setPlan(SubscriptionPlan plan) {
        this.plan = plan;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
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

    // Enums
    public enum SubscriptionPlan {
        BASIC("Basic", 1000, 10),
        PROFESSIONAL("Professional", 5000, 25),
        ENTERPRISE("Enterprise", 50000, 100);

        private final String displayName;
        private final int maxPhotos;
        private final int maxUsers;

        SubscriptionPlan(String displayName, int maxPhotos, int maxUsers) {
            this.displayName = displayName;
            this.maxPhotos = maxPhotos;
            this.maxUsers = maxUsers;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getMaxPhotos() {
            return maxPhotos;
        }

        public int getMaxUsers() {
            return maxUsers;
        }
    }
}
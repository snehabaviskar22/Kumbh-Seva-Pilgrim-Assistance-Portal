package com.kumbhseva.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "missing_persons")
public class MissingPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long lostItemId; // Reference to lost_items(id)
    private Long userId;     // Reference to users(id)

    @Column(nullable = false)
    private String fullName;

    private Integer age;
    private String gender;
    private String height;

    @Column(length = 1000)
    private String physicalDescription;

    private String lastSeenLocation;
    private LocalDateTime lastSeenDatetime;

    private String contactName;
    private String contactPhone;
    private String contactEmail;

    private String photoUrl;
    private String status = "Missing"; // Missing, Tracing, Located, Reunited, Closed

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MissingPerson() {}

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
        if (status == null) status = "Missing";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLostItemId() { return lostItemId; }
    public void setLostItemId(Long lostItemId) { this.lostItemId = lostItemId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getHeight() { return height; }
    public void setHeight(String height) { this.height = height; }

    public String getPhysicalDescription() { return physicalDescription; }
    public void setPhysicalDescription(String physicalDescription) { this.physicalDescription = physicalDescription; }

    public String getLastSeenLocation() { return lastSeenLocation; }
    public void setLastSeenLocation(String lastSeenLocation) { this.lastSeenLocation = lastSeenLocation; }

    public LocalDateTime getLastSeenDatetime() { return lastSeenDatetime; }
    public void setLastSeenDatetime(LocalDateTime lastSeenDatetime) { this.lastSeenDatetime = lastSeenDatetime; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

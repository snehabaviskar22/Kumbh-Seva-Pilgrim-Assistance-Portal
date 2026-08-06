package com.kumbhseva.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "found_items")
public class FoundItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String phone;
    private String email;
    private String itemName;
    private String category;

    @Column(length = 1000)
    private String description;

    private String locationFound;
    private LocalDateTime datetimeFound;
    private String depositedAt;

    private String imageUrl; 

    
    public Long getId() { return id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocationFound() { return locationFound; }
    public void setLocationFound(String locationFound) { this.locationFound = locationFound; }

    public LocalDateTime getDatetimeFound() { return datetimeFound; }
    public void setDatetimeFound(LocalDateTime datetimeFound) { this.datetimeFound = datetimeFound; }

    public String getDepositedAt() { return depositedAt; }
    public void setDepositedAt(String depositedAt) { this.depositedAt = depositedAt; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
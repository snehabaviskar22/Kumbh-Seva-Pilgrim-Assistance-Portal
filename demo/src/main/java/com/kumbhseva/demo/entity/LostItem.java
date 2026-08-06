
package com.kumbhseva.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lost_items")
public class LostItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String phoneNumber;
    private String email;

    private String itemName;
    private String category;

    @Column(length = 1000)
    private String description;

    private String location;
    private LocalDateTime dateTimeLost;

    private String imageUrl;

    private Integer age;
    private String height;
    private String gender;

    @Column(length = 1000)
    private String physicalIdentification;

    // ===== NEW STATUS FIELD =====
    private String status;

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getDateTimeLost() {
        return dateTimeLost;
    }

    public void setDateTimeLost(LocalDateTime dateTimeLost) {
        this.dateTimeLost = dateTimeLost;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhysicalIdentification() {
        return physicalIdentification;
    }

    public void setPhysicalIdentification(String physicalIdentification) {
        this.physicalIdentification = physicalIdentification;
    }

    // ===== STATUS GETTER/SETTER =====

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
package com.utc2.domainstore.entity.database;

import java.sql.Timestamp;

public class CustomerModel {
    private int id;
    private String fullName;
    private String email;
    private String phone;
    private String cccd;
    private String passwordHash;
    private RoleEnum role;
    private boolean isDeleted; // Thêm thuộc tính isDeleted
    private Timestamp createdAt;

    public CustomerModel() {
    }

    public CustomerModel(int id) {
        this.id = id;
    }

    public CustomerModel(String fullName, String email, String phone, String cccd, String passwordHash, RoleEnum role) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.cccd = cccd;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public CustomerModel(String phone, RoleEnum role) {
        this.phone = phone;
        this.role = role;
    }

    // Không password
    public CustomerModel(int id, String fullName, String email, String phone, String cccd, RoleEnum role, Timestamp createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.cccd = cccd;
        this.role = role;
        this.createdAt = createdAt;
    }

    public CustomerModel(int id, String fullName, String email, String phone, String cccd, String passwordHash, RoleEnum role, boolean isDeleted, Timestamp createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.cccd = cccd;
        this.passwordHash = passwordHash;
        this.role = role;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", cccd='" + cccd + '\'' +
                ", role=" + role +
                ", isDeleted=" + isDeleted +
                ", createdAt=" + createdAt +
                '}';
    }
}

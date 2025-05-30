package com.utc2.domainstore.entity.database;

import java.sql.Timestamp;

public class CustomerModel {
    private int id;
    private String fullName;
    private String email;
    private String phone;
    private String passwordHash;
    private RoleEnum role;
    private boolean isDeleted; // Thêm thuộc tính isDeleted
    private String otp;
    private Timestamp otpCreatedAt;
    private Timestamp createdAt;

    public CustomerModel() {
    }

    public CustomerModel(int id) {
        this.id = id;
    }

    public CustomerModel(String fullName, String email, String phone, String passwordHash, RoleEnum role) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public CustomerModel(String phone, RoleEnum role) {
        this.phone = phone;
        this.role = role;
    }

    // Không password
    public CustomerModel(int id, String fullName, String email, String phone, RoleEnum role, Timestamp createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.createdAt = createdAt;
    }

    public CustomerModel(int id, String fullName, String email, String phone, String passwordHash, RoleEnum role, boolean isDeleted, Timestamp createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.role = role;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
    }

    public CustomerModel(int id, String fullName, String email, String phone, String passwordHash, RoleEnum role, boolean isDeleted, String otp, Timestamp otpCreatedAt, Timestamp createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.role = role;
        this.isDeleted = isDeleted;
        this.otp = otp;
        this.otpCreatedAt = otpCreatedAt;
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


    public Timestamp getOtpCreatedAt() {
        return otpCreatedAt;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtpCreatedAt(Timestamp otpCreatedAt) {
        this.otpCreatedAt = otpCreatedAt;
    }

    public void setOtp(String otp) {
        this.otp = otp;
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
                ", role=" + role +
                ", isDeleted=" + isDeleted +
                ", createdAt=" + createdAt +
                '}';
    }
}

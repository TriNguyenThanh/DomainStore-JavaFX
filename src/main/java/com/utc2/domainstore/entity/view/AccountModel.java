package com.utc2.domainstore.entity.view;

import com.utc2.domainstore.entity.database.RoleEnum;

public class AccountModel {
    private Integer id;
    private String fullName;
    private String phone;
    private String email;
    private String hash_password;
    private RoleEnum role;

    public AccountModel() {

    }

    public AccountModel(String fullName, String phone, String email, String hash_password) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.hash_password = hash_password;
    }

    public AccountModel(String fullName, String phone, String email, String hash_password, RoleEnum role) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.hash_password = hash_password;
        this.role = role;
    }

    public AccountModel(Integer id, String fullName, String phone, String email, String psID, String hash_password, RoleEnum role) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.hash_password = hash_password;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHash_password() {
        return hash_password;
    }

    public void setHash_password(String hash_password) {
        this.hash_password = hash_password;
    }

    public void copy(String fullName, String phone, String email, String hash_password) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.hash_password = hash_password;
    }

    public boolean isSame(AccountModel accountModel) {
        if (!accountModel.fullName.equals(this.fullName)) return false;
        else if (!accountModel.phone.equals(this.phone)) return false;
        else if (!accountModel.email.equals(this.email)) return false;
        return true;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }
}

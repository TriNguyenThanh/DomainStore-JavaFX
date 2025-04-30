package com.utc2.domainstore.entity.view;

import com.utc2.domainstore.entity.database.RoleEnum;

public class UserModel {
    private int ID;
    private String name;
    private String phone;
    private String email;
    private String psID;
    private RoleEnum role;
    private ACCOUNT_STATUS status;

    public UserModel() {

    }

    public UserModel(int ID, String name, String phone, String email, String psID, RoleEnum role, ACCOUNT_STATUS status) {
        this.ID = ID;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.psID = psID;
        this.role = role;
        this.status = status;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPsID() {
        return psID;
    }

    public void setPsID(String psID) {
        this.psID = psID;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    public ACCOUNT_STATUS getStatus() {
        return status;
    }

    public void setStatus(ACCOUNT_STATUS status) {
        this.status = status;
    }
}

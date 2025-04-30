package com.utc2.domainstore.entity.view;

import com.utc2.domainstore.entity.database.RoleEnum;

public class UserModel {
    private Integer ID;
    private String name;
    private String phone;
    private String email;
    private String psID;
    private RoleEnum role;
    private ACCOUNT_STATUS status;
    private String password;

    public UserModel() {

    }

    public UserModel(Integer ID, String name, String phone, String email, String psID, RoleEnum role, ACCOUNT_STATUS status, String password) {
        this.ID = ID;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.psID = psID;
        this.role = role;
        this.status = status;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
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

    public void copy(UserModel userModel) {
        this.ID = userModel.getID();
        this.name = userModel.getName();
        this.phone = userModel.getPhone();
        this.email = userModel.getEmail();
        this.psID = userModel.getPsID();
        this.role = userModel.getRole();
        this.status = userModel.getStatus();
        this.password = userModel.getPassword();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        UserModel userModel = (UserModel) obj;

        if (!ID.equals(userModel.ID)) return false;
        if (!name.equals(userModel.name)) return false;
        if (!phone.equals(userModel.phone)) return false;
        if (!email.equals(userModel.email)) return false;
        if (!psID.equals(userModel.psID)) return false;
        if (role != userModel.role) return false;
        if (status != userModel.status) return false;

        return true;
    }
}

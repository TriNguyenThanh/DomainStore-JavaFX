package com.utc2.domainstore.entity.view;

public class AccountModel {
    private String fullName;
    private String phone;
    private String email;
    private String psID;
    private String hash_password;

    public AccountModel() {

    }

    public AccountModel(String fullName, String phone, String email, String psID, String hash_password) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.psID = psID;
        this.hash_password = hash_password;
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

    public String getPsID() {
        return psID;
    }

    public void setPsID(String psID) {
        this.psID = psID;
    }

    public String getHash_password() {
        return hash_password;
    }

    public void setHash_password(String hash_password) {
        this.hash_password = hash_password;
    }

    public void copy(String fullName, String phone, String email, String psID, String hash_password) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.psID = psID;
        this.hash_password = hash_password;
    }

    public boolean isSame(AccountModel accountModel) {
        if (!accountModel.fullName.equals(this.fullName)) return false;
        else if (!accountModel.phone.equals(this.phone)) return false;
        else if (!accountModel.email.equals(this.email)) return false;
        else if (!accountModel.psID.equals(this.psID)) return false;
        return true;
    }
}

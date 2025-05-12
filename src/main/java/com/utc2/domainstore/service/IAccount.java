package com.utc2.domainstore.service;

import org.json.JSONObject;

public interface IAccount {
    public JSONObject getUserInformation(JSONObject t);
    public JSONObject updateUser(JSONObject t);
    public JSONObject updateUserPassword(JSONObject t);
    public JSONObject getAllUserAccount();
    public JSONObject lockedAccount(JSONObject t);
    public JSONObject sendOtpToUser(JSONObject t);
    public JSONObject checkingOtp(JSONObject t);
    public JSONObject updatingNewPassWord(JSONObject t);
}

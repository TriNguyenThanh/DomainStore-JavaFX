package com.utc2.domainstore.service;

import org.json.JSONObject;

public interface IAccount {
    public JSONObject getUserInformation(JSONObject t);
    public JSONObject updateUser(JSONObject t);
    public JSONObject updateUserPassword(JSONObject t);
    public JSONObject getAllUserAccount();
}

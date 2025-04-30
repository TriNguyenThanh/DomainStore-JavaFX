package com.utc2.domainstore.service;

import org.json.JSONObject;

public interface ITopLevelDomain {
    public JSONObject updateTLD(JSONObject jsonInput);
    public JSONObject getAllTLD();
}

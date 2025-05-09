package com.utc2.domainstore.service;

import org.json.JSONObject;

public interface IDomain {
    public JSONObject search(JSONObject jsonInput);
    public JSONObject suggestion(JSONObject jsonInput);
    public JSONObject searchSoldDomainByCusId(JSONObject jsonInput);
    public JSONObject getAllDomains();
    public JSONObject insertNewDomain(JSONObject jsonInput);
    public JSONObject deleteAvailableDomain(JSONObject jsonObject);
}

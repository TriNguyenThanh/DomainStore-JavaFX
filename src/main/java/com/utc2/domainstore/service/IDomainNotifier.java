package com.utc2.domainstore.service;

import org.json.JSONArray;
import org.json.JSONObject;

public interface IDomainNotifier {
    public JSONObject notifyExpiringDomains(JSONArray jsonInput);
    public JSONObject getExpiringDomainsAsJson();
}

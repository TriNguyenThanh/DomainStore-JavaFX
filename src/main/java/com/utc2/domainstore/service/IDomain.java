package com.utc2.domainstore.service;

import org.json.JSONObject;

public interface IDomain {
    public JSONObject search(JSONObject jsonInput);
//    public JSONObject getShoppingCart(JSONObject jsonInput);
//    public JSONObject addToCart(JSONObject jsonInput);
    public JSONObject suggestion(JSONObject jsonInput);
}

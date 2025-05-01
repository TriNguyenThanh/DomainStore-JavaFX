package com.utc2.domainstore.service;

import org.json.JSONObject;

public interface ICart {
    public JSONObject getShoppingCart(JSONObject jsonInput);
    public JSONObject addToCart(JSONObject jsonInput);
    public JSONObject removeFromCart(JSONObject jsonInput);
}

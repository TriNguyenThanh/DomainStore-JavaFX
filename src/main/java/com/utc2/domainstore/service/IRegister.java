package com.utc2.domainstore.service;

import org.json.JSONObject;

import java.sql.SQLException;

public interface IRegister {
    public JSONObject addToDB(JSONObject jsonInput) throws SQLException;
}

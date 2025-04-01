package com.utc2.domainstore.service;

import org.json.JSONObject;

public interface ITransactionService {
    public JSONObject getAllTransaction();
    public JSONObject getAllUserTransaction(JSONObject json);
    public JSONObject getTransactionInfomation(JSONObject json);

}

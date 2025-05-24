package com.utc2.domainstore.service;

import com.utc2.domainstore.entity.database.TransactionStatusEnum;
import org.json.JSONObject;

import java.io.IOException;

public interface ITransactionService {
    public JSONObject getAllTransaction();

    public JSONObject getAllUserTransaction(JSONObject json);

    public JSONObject getTransactionInfomation(JSONObject json);

    public JSONObject createTransaction(JSONObject json) throws IOException;

    public void updateTransactionStatus(String transactionId, TransactionStatusEnum status);
}
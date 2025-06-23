package com.utc2.domainstore.service;

import com.utc2.domainstore.entity.database.TransactionStatusEnum;
import org.json.JSONObject;

import java.io.IOException;

public interface ITransactionService {
    JSONObject getAllTransaction();

    JSONObject getAllUserTransaction(JSONObject json);

    JSONObject getTransactionInfomation(JSONObject json);

    JSONObject createTransaction(JSONObject json) throws IOException;

    void updateTransactionStatus(String transactionId, TransactionStatusEnum status);
}
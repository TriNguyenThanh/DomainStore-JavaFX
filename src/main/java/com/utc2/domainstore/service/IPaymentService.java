package com.utc2.domainstore.service;

import org.json.JSONObject;

import java.io.IOException;

public interface IPaymentService {
    JSONObject getUserPaymentHistory(JSONObject json);
    boolean createPayment(String transactionId, int total) throws IOException;
}

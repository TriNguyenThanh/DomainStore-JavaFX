package com.utc2.domainstore.service;

import org.json.JSONObject;

import java.io.IOException;

public interface IPaymentService {
    JSONObject getUserPaymentHistory(JSONObject json);
    JSONObject getTransactionPaymentHistory(JSONObject json);
    JSONObject createPayment(JSONObject json) throws IOException;
}

package com.utc2.domainstore.service;

import java.util.Map;

public interface PaymentListener {
    void onPaymentProcessed(Map<String, String> paymentResult);
}
package com.utc2.domainstore.service;

import java.util.Map;

public interface IPaymentGateway {
    String createPaymentUrl(Long amount, String orderId, String tnxId);
    Map<String, String> processReturnUrl(Map<String, String> fields);
}

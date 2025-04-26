package com.utc2.domainstore.config;

public class VnPayConfig {
    public static final String VNP_TMN_CODE = "CIDAB46E";
    public static final String VNP_HASH_SECRET = "6JHQN2YMPDXOQ537EOOHNZ81H4BVPR3E";
    public static final String VNP_VERSION = "2.1.0";
    public static final String VNP_COMMAND = "pay";
    public static final String VNP_RETURN_URL = "http://localhost:8080/vnpay_return"; // URL để VNPay gọi lại sau khi thanh toán
    public static final String VNP_API_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
}

package com.utc2.domainstore.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMACUtil {
    public static final String HMACSHA256 = "HmacSHA256";

    public static String HMacHexStringEncode(String algorithm, String key, String data) {
        try {
            Mac mac = Mac.getInstance(algorithm);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), algorithm);
            mac.init(secretKey);
            byte[] bytes = mac.doFinal(data.getBytes("UTF-8"));
            return bytesToHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

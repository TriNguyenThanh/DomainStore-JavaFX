package com.utc2.domainstore.service;

import com.utc2.domainstore.config.VnPayConfig;
import com.utc2.domainstore.utils.VnPayUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class VnPayService implements IPaymentGateway{
    private VnPayConfig vnPayConfig = new VnPayConfig();
    private VnPayUtils vnPayUtils = new VnPayUtils();
    @Override
    public String createPaymentUrl(int amount, String orderId, String tnxId) {
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnPayConfig.VNP_VERSION);
        vnp_Params.put("vnp_Command", vnPayConfig.VNP_COMMAND);
        vnp_Params.put("vnp_TmnCode", vnPayConfig.VNP_TMN_CODE);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // Amount in VND, multiply by 100
        vnp_Params.put("vnp_CurrCode", "VND");

        // Generate transaction date in VNPay format (yyyyMMddHHmmss)
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        vnp_Params.put("vnp_IpAddr", "127.0.0.1"); // Should be client IP in production
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_OrderInfo", orderId);
        vnp_Params.put("vnp_OrderType", "other"); // Default order type
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.VNP_RETURN_URL); // URL to handle return from VNPay
        vnp_Params.put("vnp_TxnRef", tnxId);

        // Optional: Add bank code if needed
        // vnp_Params.put("vnp_BankCode", "NCB");

        // Build query string
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String vnp_SecureHash = vnPayUtils.hmacSHA512(vnPayConfig.VNP_HASH_SECRET, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        return vnPayConfig.VNP_API_URL + "?" + query.toString();
    }

    @Override
    public Map<String, String> processReturnUrl(Map<String, String> fields) {
        Map<String, String> result = new HashMap<>();

        try {
            // Remove vnp_SecureHash from fields
            String vnp_SecureHash = fields.get("vnp_SecureHash");
            if (vnp_SecureHash == null) {
                result.put("status", "invalid");
                result.put("message", "Không tìm thấy chữ ký bảo mật");
                return result;
            }

            Map<String, String> fieldsCopy = new HashMap<>(fields);
            fieldsCopy.remove("vnp_SecureHash");
            fieldsCopy.remove("vnp_SecureHashType");

            // Check checksum
            String signValue = vnPayUtils.calculateChecksum(fieldsCopy, vnPayConfig.VNP_HASH_SECRET);

            if (signValue.equals(vnp_SecureHash)) {
                // Valid signature
                String vnp_ResponseCode = fields.get("vnp_ResponseCode");
                if ("00".equals(vnp_ResponseCode)) {
                    // Payment successful
                    result.put("status", "success");
                    result.put("message", "Thanh toán thành công");
                    result.put("txnRef", fields.get("vnp_TxnRef"));
                    result.put("amount", String.valueOf(Long.parseLong(fields.get("vnp_Amount")) / 100));
                    result.put("orderInfo", fields.get("vnp_OrderInfo"));
                    result.put("payDate", fields.get("vnp_PayDate"));
                    result.put("transactionNo", fields.get("vnp_TransactionNo"));

                    // Cập nhật trạng thái đơn hàng
//                    updateOrderStatus(fields.get("vnp_TxnRef"), "Đã thanh toán", fields.get("vnp_TransactionNo"));
                } else {
                    // Payment failed
                    result.put("status", "failed");
                    result.put("message", "Thanh toán thất bại. Mã lỗi: " + vnp_ResponseCode);
                    result.put("txnRef", fields.get("vnp_TxnRef"));
                }
            } else {
                // Invalid signature
                result.put("status", "invalid");
                result.put("message", "Chữ ký không hợp lệ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", "Lỗi xử lý: " + e.getMessage());
        }
        return result;
    }
    static String createResponseHTML(Map<String, String> paymentResult) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><title>VNPay Payment Result</title>");
        html.append("<meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;margin:40px;line-height:1.6;}");
        html.append(".container{max-width:600px;margin:0 auto;padding:20px;border:1px solid #ddd;border-radius:5px;}");
        html.append(".success{color:green;} .failed{color:red;}</style></head>");
        html.append("<body><div class='container'>");

        String status = paymentResult.get("status");
        if ("success".equals(status)) {
            html.append("<h1 class='success'>Thanh toán thành công</h1>");
            html.append("<p>Mã đơn hàng: ").append(paymentResult.get("txnRef")).append("</p>");
            html.append("<p>Số tiền: ").append(paymentResult.get("amount")).append(" VND</p>");
            html.append("<p>Nội dung thanh toán: ").append(paymentResult.get("orderInfo")).append("</p>");
            html.append("<p>Thời gian: ").append(paymentResult.get("payDate")).append("</p>");
            html.append("<p>Mã giao dịch: ").append(paymentResult.get("transactionNo")).append("</p>");
        } else {
            html.append("<h1 class='failed'>Thanh toán thất bại</h1>");
            html.append("<p>").append(paymentResult.get("message")).append("</p>");
            if (paymentResult.containsKey("txnRef")) {
                html.append("<p>Mã đơn hàng: ").append(paymentResult.get("txnRef")).append("</p>");
            }
        }
        html.append("</div>");
        html.append("</body></html>");
        return html.toString();
    }

    /**
     * Gửi request GET đến URL
     */
    private static String sendGetRequest(String url) {
        StringBuilder response = new StringBuilder();

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.toString();
    }
}

package com.utc2.domainstore.service;

import com.sun.net.httpserver.HttpServer;
import com.utc2.domainstore.entity.database.PaymentHistoryModel;
import com.utc2.domainstore.entity.database.PaymentStatusEnum;
import com.utc2.domainstore.entity.database.PaymentTypeEnum;
import com.utc2.domainstore.entity.database.TransactionStatusEnum;
import com.utc2.domainstore.repository.PaymentHistoryRepository;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

public class ZaloPayService implements IPaymentGateway{
    private static final String APP_ID = "2553";
    private static final String KEY1 = "PcY4iZIKFCIdgZvA6ueMcMHHUbRLYjPL";
    private static final String KEY2 = "kLtgPl8HHhfvMuDHPwKfgfsY4Ydm9eIz";
    private static final String CREATE_ORDER_URL = "https://sb-openapi.zalopay.vn/v2/create";
    private static final String CALLBACK_URL = "http://localhost:8080/zalopay";
    protected static String TransactionID;
    @Override
    public String createPaymentUrl(Long amount, String transactionId, String txnRef) {
        try {
            Map<String, String> params = new HashMap<>();
//            long appTime = System.currentTimeMillis();
            String appTransId = generateAppTransId();
            params.put("app_id", APP_ID);
            params.put("app_user", "demo_user"); // Should be unique user ID in production
//            params.put("app_time", String.valueOf(appTime));
            params.put("app_time", txnRef);
            params.put("app_trans_id", appTransId);
            params.put("amount", String.valueOf(amount));
            params.put("description", "Thanh toán hoá đơn " + transactionId);
            params.put("bank_code", ""); // Optional, leave empty for user to choose
            params.put("embed_data", "{\"redirecturl\":\"http://localhost:8080/zalopay\"}"); // Optional JSON data
            params.put("item", "[]"); // Optional JSON array of items
            params.put("callback_url", CALLBACK_URL);

            // Generate MAC (checksum)
            String data = params.get("app_id") + "|" +
                    params.get("app_trans_id") + "|" +
                    params.get("app_user") + "|" +
                    params.get("amount") + "|" +
                    params.get("app_time") + "|" +
                    params.get("embed_data") + "|" +
                    params.get("item");
            String mac = hmacSHA256(KEY1, data);
            params.put("mac", mac);

            // Send POST request to ZaloPay
            String response = sendPostRequest(CREATE_ORDER_URL, params);
            JSONObject jsonResponse = new JSONObject(response);

            int returnCode = jsonResponse.getInt("return_code");
            if (returnCode == 1) {
                String url = jsonResponse.getString("order_url");
//                System.out.println("Tạo URL thành công: " + url);
                return url;
            } else {
                System.out.println("Tạo URL thất bại: " + jsonResponse.getString("return_message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println( "Lỗi khi tạo URL: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Map<String, String> processReturnUrl(Map<String, String> fields) {
        Map<String, String> result = new HashMap<>();
        TransactionService transactionService = new TransactionService();
        try {
            String status = fields.get("status");
            String appTransId = fields.get("apptransid");
            if("1".equals(status)){
                result.put("status", "success");
                result.put("message", "Thanh toán thành công");
                result.put("apptransid", appTransId);
                result.put("amount", fields.get("amount"));
                result.put("transactionId", TransactionID);

                PaymentHistoryModel paymentHistoryModel = new PaymentHistoryModel(TransactionID, appTransId,
                        PaymentTypeEnum.ZALOPAY.getCode(), PaymentStatusEnum.COMPLETED, Timestamp.valueOf(LocalDateTime.now()));
                PaymentHistoryRepository.getInstance().insert(paymentHistoryModel);
                // bỏ vì frontend đã xử lý rồi, nếu không có frontend xử lý thì mở
//                transactionService.updateTransactionStatus(TransactionID, TransactionStatusEnum.COMPLETED);
            }else{
                result.put("status", "failed");
                result.put("message", "Thanh toán thất bại");
                result.put("apptransid", appTransId);
                result.put("amount", fields.get("amount"));
                result.put("transactionId", TransactionID);
//                transactionService.updateTransactionStatus(TransactionID, TransactionStatusEnum.PAYMENT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", "Error parsing callback: " + e.getMessage());
//            transactionService.updateTransactionStatus(TransactionID, TransactionStatusEnum.CANCELLED);
        }
        return result;
    }
    public static String hmacSHA256(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(secretKeySpec);
            byte[] hmacData = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Convert to hex
            StringBuilder result = new StringBuilder();
            for (byte b : hmacData) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Send POST request with form data
     */
    private static String sendPostRequest(String url, Map<String, String> params) {
        StringBuilder response = new StringBuilder();
        try {
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            // Build form data
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8.toString()));
                postData.append('=');
                postData.append(URLEncoder.encode(param.getValue(), StandardCharsets.UTF_8.toString()));
            }

            // Send request
            OutputStream os = conn.getOutputStream();
            os.write(postData.toString().getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

            // Read response
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } else {
                response.append("{\"return_code\": \"0\", \"return_message\": \"HTTP Error " + responseCode + "\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.append("{\"return_code\": \"0\", \"return_message\": \"Exception: " + e.getMessage() + "\"}");
        }
        return response.toString();
    }
    private static String generateAppTransId() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        String date = dateFormat.format(new Date());
        String random = String.format("%06d", new Random().nextInt(1000000));
        return date + "_" + random;
    }
    public static String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }
    static String createResponseHTML(Map<String, String> paymentResult, String paymentURL) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang='vi'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>Kết quả thanh toán</title>");
        html.append("<script src='https://cdn.tailwindcss.com'></script>");
        html.append("<link href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css' rel='stylesheet'>");
        html.append("<style>");
        html.append("@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }");
        html.append(".fade-in { animation: fadeIn 0.8s ease-out; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body class='bg-gray-100 flex items-center justify-center min-h-screen'>");
        html.append("<div class='container max-w-lg mx-auto p-6 bg-white rounded-xl shadow-lg fade-in'>");

        String status = paymentResult.get("status");
        if ("success".equals(status)) {
            html.append("<div class='text-center'>");
            html.append("<i class='fas fa-check-circle text-green-500 text-5xl mb-4'></i>");
            html.append("<h1 class='text-2xl font-bold text-green-600 mb-4'>Thanh toán thành công!</h1>");
            html.append("</div>");
            html.append("<div class='space-y-4'>");
            html.append("<div class='flex justify-between border-b pb-2'>");
            html.append("<span class='text-gray-600 font-medium'>Mã đơn hàng:</span>");
            html.append("<span class='text-gray-800'>").append(paymentResult.get("transactionId")).append("</span>");
            html.append("</div>");
            html.append("<div class='flex justify-between border-b pb-2'>");
            html.append("<span class='text-gray-600 font-medium'>Số tiền:</span>");
            html.append("<span class='text-gray-800'>");
            String amountStr = paymentResult.get("amount");
            Long amount = Long.parseLong(amountStr);
            html.append(String.format("%,d", amount)).append(" VND</span>");
            html.append("</div>");
            html.append("<div class='flex justify-between border-b pb-2'>");
            html.append("<span class='text-gray-600 font-medium'>Nội dung thanh toán:</span>");
            html.append("<span class='text-gray-800'>").append("Thanh toán hoá đơn ").append(paymentResult.get("transactionId")).append("</span>");
            html.append("</div>");
            html.append("<div class='flex justify-between border-b pb-2'>");
            html.append("<span class='text-gray-600 font-medium'>Thời gian:</span>");
            html.append("<span class='text-gray-800'>").append(getCurrentTimeString("HH:mm:ss dd/MM/yyyy")).append("</span>");
            html.append("</div>");
            html.append("<div class='flex justify-between border-b pb-2'>");
            html.append("<span class='text-gray-600 font-medium'>Mã giao dịch:</span>");
            html.append("<span class='text-gray-800'>").append(paymentResult.get("apptransid")).append("</span>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class='mt-6 text-center'>");
            html.append("<a href='https://docs.zalopay.vn/v2/' class='inline-block bg-green-600 text-white font-semibold py-2 px-6 rounded-lg hover:bg-green-700 transition duration-300'>Về trang chủ</a>");
            html.append("</div>");
        } else {
            html.append("<div class='text-center'>");
            html.append("<i class='fas fa-times-circle text-red-500 text-5xl mb-4'></i>");
            html.append("<h1 class='text-2xl font-bold text-red-600 mb-4'>Thanh toán thất bại</h1>");
            html.append("</div>");
            html.append("<div class='space-y-4'>");
            html.append("<div class='text-center text-gray-600'>");
            html.append("<p>").append(paymentResult.get("message")).append("</p>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class='mt-6 text-center'>");
            html.append("<a href='").append(paymentURL).append("'").append(" class='inline-block bg-red-600 text-white font-semibold py-2 px-6 rounded-lg hover:bg-red-700 transition duration-300'>Thử lại</a>");
            html.append("</div>");
        }

        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }
}

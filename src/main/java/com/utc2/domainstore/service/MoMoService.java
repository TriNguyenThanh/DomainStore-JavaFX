package com.utc2.domainstore.service;

import com.utc2.domainstore.entity.database.PaymentHistoryModel;
import com.utc2.domainstore.entity.database.PaymentStatusEnum;
import com.utc2.domainstore.entity.database.PaymentTypeEnum;
import com.utc2.domainstore.entity.database.TransactionStatusEnum;
import com.utc2.domainstore.repository.PaymentHistoryRepository;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.utc2.domainstore.service.ZaloPayService.getCurrentTimeString;

public class MoMoService implements IPaymentGateway{
    // MoMo configuration - replace with actual values from MoMo
    private static final String PARTNER_CODE = "MOMO"; // MoMo Partner Code
    private static final String ACCESS_KEY = "F8BBA842ECF85"; // MoMo Access Key
    private static final String SECRET_KEY = "K951B6PE1waDMi640xX08PD3vg6EkVlz"; // MoMo Secret Key
    private static final String CREATE_ORDER_URL = "https://test-payment.momo.vn/v2/gateway/api/create";
    private static final String IPN_URL = "http://localhost:8080/momo";
    private static final String REDIRECT_URL = "http://localhost:8080/momo";
    @Override
    public String createPaymentUrl(Long amount, String transactionId, String tnxId) {
        String orderId = transactionId + "_" + tnxId;
        String requestId = orderId +"_req";
        Map<String, String> result = new HashMap<>();
        try {
            JSONObject params = new JSONObject();
            params.put("partnerCode", PARTNER_CODE);
            params.put("accessKey", ACCESS_KEY);
            params.put("requestId", requestId);
            params.put("amount", String.valueOf(amount));
            params.put("orderId", orderId);
            params.put("orderInfo", "Thanh toán hoá đơn " + transactionId);
            params.put("redirectUrl", REDIRECT_URL);
            params.put("ipnUrl", IPN_URL);
            params.put("requestType", "captureWallet");
            params.put("extraData", ""); // Optional
            params.put("lang", "vi");

            // Generate signature
            String rawSignature = "accessKey=" + ACCESS_KEY +
                    "&amount=" + amount +
                    "&extraData=" + "" +
                    "&ipnUrl=" + IPN_URL +
                    "&orderId=" + orderId +
                    "&orderInfo=" + "Thanh toán hoá đơn " + transactionId +
                    "&partnerCode=" + PARTNER_CODE +
                    "&redirectUrl=" + REDIRECT_URL +
                    "&requestId=" + requestId +
                    "&requestType=captureWallet";
            String signature = hmacSHA256(SECRET_KEY, rawSignature);
            params.put("signature", signature);

            // Send POST request to MoMo
            String response = sendPostRequest(CREATE_ORDER_URL, params.toString());
            JSONObject jsonResponse = new JSONObject(response);

            // Handle resultCode as a potential integer
            String resultCode = String.valueOf(jsonResponse.get("resultCode"));
            if ("0".equals(resultCode)) {
                return jsonResponse.getString("payUrl");
            } else {
                result.put("status", "failed");
                result.put("message", jsonResponse.getString("message"));
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
        String orderId = fields.get("orderId");
        String requestId = fields.get("requestId");
        String amount = fields.get("amount");
        String transId = fields.get("transId");
        String resultCode = String.valueOf(fields.get("resultCode"));

        TransactionService transactionService = new TransactionService();
        int n = orderId.indexOf('_');
        String transactionId = orderId.substring(0,n);
        try {
            // Process based on resultCode
            if ("0".equals(resultCode)) {
                result.put("status", "success");
                result.put("message", "Thanh toán thành công");
                result.put("orderId", orderId);
                result.put("amount", amount);
                result.put("transId", transId);
                result.put("requestId", requestId);

                // Update order status
                PaymentHistoryModel paymentHistoryModel = new PaymentHistoryModel(transactionId, transId,
                        PaymentTypeEnum.MOMO.getCode(), PaymentStatusEnum.COMPLETED, Timestamp.valueOf(LocalDateTime.now()));
                PaymentHistoryRepository.getInstance().insert(paymentHistoryModel);
                transactionService.updateTransactionStatus(transactionId, TransactionStatusEnum.COMPLETED);
            } else {
                result.put("status", "failed");
                result.put("message", "Thanh toán thất bại. Lỗi: " + resultCode);
                result.put("orderId", orderId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", "Error parsing callback: " + e.getMessage());
            transactionService.updateTransactionStatus(transactionId, TransactionStatusEnum.CANCELLED);
        }
        return result;
    }
    /**
     * Calculate HMAC-SHA256
     */
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
     * Send POST request with JSON body
     */
    private static String sendPostRequest(String url, String jsonBody) {
        StringBuilder response = new StringBuilder();
        try {
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send request
            OutputStream os = conn.getOutputStream();
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
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
                response.append("{\"resultCode\": \"99\", \"message\": \"HTTP Error " + responseCode + "\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.append("{\"resultCode\": \"99\", \"message\": \"Exception: " + e.getMessage() + "\"}");
        }
        return response.toString();
    }
    public String createResponseHTML(Map<String, String> paymentResult, String paymentURL) {
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
            String orderId = paymentResult.get("orderId");
            int n = orderId.indexOf('_');
            String transactionId = orderId.substring(0,n);
            html.append("<span class='text-gray-800'>").append(transactionId).append("</span>");
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
            html.append("<span class='text-gray-800'>").append("Thanh toán hoá đơn ").append(transactionId).append("</span>");
            html.append("</div>");
            html.append("<div class='flex justify-between border-b pb-2'>");
            html.append("<span class='text-gray-600 font-medium'>Thời gian:</span>");
            html.append("<span class='text-gray-800'>").append(getCurrentTimeString("HH:mm:ss dd/MM/yyyy")).append("</span>");
            html.append("</div>");
            html.append("<div class='flex justify-between border-b pb-2'>");
            html.append("<span class='text-gray-600 font-medium'>Mã giao dịch:</span>");
            html.append("<span class='text-gray-800'>").append(paymentResult.get("transId")).append("</span>");
            html.append("</div>");
            html.append("</div>");
            html.append("<div class='mt-6 text-center'>");
            html.append("<a href='https://vnpay.vn/' class='inline-block bg-green-600 text-white font-semibold py-2 px-6 rounded-lg hover:bg-green-700 transition duration-300'>Về trang chủ</a>");
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

package com.utc2.domainstore.service;

import com.utc2.domainstore.config.VnPayConfig;
import com.utc2.domainstore.entity.database.*;
import com.utc2.domainstore.repository.PaymentHistoryRepository;
import com.utc2.domainstore.repository.TransactionInfoRepository;
import com.utc2.domainstore.utils.VnPayUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        TransactionService transactionService = new TransactionService();
        String transactionId = fields.get("vnp_OrderInfo");
        try {
            // Remove vnp_SecureHash from fields
            String vnp_SecureHash = fields.get("vnp_SecureHash");
            if (vnp_SecureHash == null) {
                result.put("status", "invalid");
                result.put("message", "Không tìm thấy chữ ký bảo mật");
                transactionService.updateTransactionStatus(transactionId, TransactionStatusEnum.CANCELLED);
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
                    String datetime = fields.get("vnp_PayDate");
                    DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                    LocalDateTime parsedDateTime = LocalDateTime.parse(datetime, inputFormat);

                    DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
                    String formattedDate = parsedDateTime.format(outputFormat);
                    result.put("payDate", formattedDate );
                    result.put("transactionNo", fields.get("vnp_TransactionNo"));

                    // Tạo thanh toán, chuyển tới trang thanh toán
                    PaymentHistoryModel paymentHistoryModel = new PaymentHistoryModel(transactionId, fields.get("vnp_TransactionNo"),
                            PaymentTypeEnum.VNPAY.getCode(), PaymentStatusEnum.COMPLETED, LocalDate.now());
                    PaymentHistoryRepository.getInstance().insert(paymentHistoryModel);
                } else {
                    // Payment failed
                    result.put("status", "failed");
                    result.put("message", "Thanh toán thất bại. Mã lỗi: " + vnp_ResponseCode);
                    result.put("txnRef", fields.get("vnp_TxnRef"));
                    transactionService.updateTransactionStatus(transactionId, TransactionStatusEnum.PENDINGPAYMENT);
                }
            } else {
                // Invalid signature
                result.put("status", "invalid");
                result.put("message", "Chữ ký không hợp lệ");
                transactionService.updateTransactionStatus(transactionId, TransactionStatusEnum.CANCELLED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", "Lỗi xử lý: " + e.getMessage());
        }
        return result;
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

        String amountStr = paymentResult.get("amount");
        Long amount = Long.parseLong(amountStr);
        String status = paymentResult.get("status");
        if ("success".equals(status)) {
            html.append("<div class='text-center'>");
            html.append("<i class='fas fa-check-circle text-green-500 text-5xl mb-4'></i>");
            html.append("<h1 class='text-2xl font-bold text-green-600 mb-4'>Thanh toán thành công!</h1>");
            html.append("</div>");
            html.append("<div class='space-y-4'>");
            html.append("<div class='flex justify-between border-b pb-2'>");
            html.append("<span class='text-gray-600 font-medium'>Mã đơn hàng:</span>");
            html.append("<span class='text-gray-800'>").append(paymentResult.get("txnRef")).append("</span>");
            html.append("</div>");
            html.append("<div class='flex justify-between border-b pb-2'>");
            html.append("<span class='text-gray-600 font-medium'>Số tiền:</span>");
            html.append("<span class='text-gray-800'>");
            html.append(String.format("%,d", amount)).append(" VND</span>");
            html.append("</div>");
            html.append("<div class='flex justify-between border-b pb-2'>");
            html.append("<span class='text-gray-600 font-medium'>Nội dung thanh toán:</span>");
            html.append("<span class='text-gray-800'>").append(paymentResult.get("orderInfo")).append("</span>");
            html.append("</div>");
            html.append("<div class='flex justify-between border-b pb-2'>");
            html.append("<span class='text-gray-600 font-medium'>Thời gian:</span>");
            html.append("<span class='text-gray-800'>").append(paymentResult.get("payDate")).append("</span>");
            html.append("</div>");
            html.append("<div class='flex justify-between border-b pb-2'>");
            html.append("<span class='text-gray-600 font-medium'>Mã giao dịch:</span>");
            html.append("<span class='text-gray-800'>").append(paymentResult.get("transactionNo")).append("</span>");
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
//            if (paymentResult.containsKey("txnRef")) {
//                html.append("<div class='flex justify-between border-b pb-2'>");
//                html.append("<span class='text-gray-600 font-medium'>Mã đơn hàng:</span>");
//                html.append("<span class='text-gray-800'>").append(paymentResult.get("txnRef")).append("</span>");
//                html.append("</div>");
//            }
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

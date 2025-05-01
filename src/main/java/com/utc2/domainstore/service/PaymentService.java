package com.utc2.domainstore.service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.utc2.domainstore.config.VnPayConfig;
import com.utc2.domainstore.entity.database.PaymentHistoryModel;
import com.utc2.domainstore.entity.database.PaymentTypeEnum;
import com.utc2.domainstore.repository.PaymentHistoryRepository;
import com.utc2.domainstore.utils.VnPayUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

public class PaymentService implements IPaymentService {
    private ArrayList<PaymentHistoryModel> listPaymentHistory = PaymentHistoryRepository.getInstance().selectAll();
    private final PaymentHistoryRepository paymentHistoryDAO = new PaymentHistoryRepository();
    private VnPayConfig vnPayConfig = new VnPayConfig();
    private static VnPayService vnPayService = new VnPayService();
    private static String paymentURL;

    private static PaymentListener listener; // thêm dòng này

    public void setListener(PaymentListener l) {
        listener = l;
    }

    @Override
    public JSONObject getUserPaymentHistory(JSONObject json) {
        int userId = json.getInt("user_id");
        JSONArray jsonArray = new JSONArray();
        for (PaymentHistoryModel p : paymentHistoryDAO.selectByCondition("user_id = " + userId)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("payment_id", p.getPaymentId());
            jsonObject.put("transaction_id", p.getTransactionId());
            jsonObject.put("method", PaymentTypeEnum.getPaymentMethod(p.getPaymentMethodId()));
            jsonObject.put("date", p.getPaymentDate());
            jsonObject.put("status", p.getPaymentStatus());
            jsonArray.put(jsonObject);
        }
        JSONObject result = new JSONObject();
        result.put("paymentHistory", jsonArray);
        return result;
    }

    @Override
    public boolean createPayment(JSONObject json) throws IOException {
        // request: total (int), transactionId (String)
        // response: true / false (boolean)

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/vnpay_return", new VNPayReturnHandler());
        server.setExecutor(null); // Sử dụng executor mặc định
        server.start();
        // Tạo transaction reference là timestamp hiện tại
        String txnRef = String.valueOf(System.currentTimeMillis());
        // Tạo URL thanh toán
        paymentURL = vnPayService.createPaymentUrl(json.getInt("total"), json.getString("transactionId"), txnRef);
        try {
            // Kiểm tra xem Desktop có được hỗ trợ không
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                URI uri = new URI(paymentURL);
                // Mở URL trong trình duyệt mặc định
                Desktop.getDesktop().browse(uri);
                return true;
            } else {
                System.out.println("Desktop không được hỗ trợ trên hệ thống này.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static class VNPayReturnHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Lấy URI chứa các tham số trả về từ VNPay
            URI requestURI = exchange.getRequestURI();
            String query = requestURI.getQuery();

            System.out.println("Nhận callback từ VNPay: " + query);

            // Parse query string thành Map
            Map<String, String> parameters = VnPayUtils.parseQueryString(query);

            // Xử lý kết quả thanh toán
            Map<String, String> paymentResult = vnPayService.processReturnUrl(parameters);

            // Tạo response HTML
            String response = vnPayService.createResponseHTML(paymentResult, paymentURL);

            // Gửi response về cho client
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

            // Gọi listener để thông báo kết quả thanh toán
            if (listener != null) {
                listener.onPaymentProcessed(paymentResult);
            }

            // In kết quả ra console
            System.out.println("Kết quả xử lý thanh toán:");
            for (Map.Entry<String, String> entry : paymentResult.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }
}

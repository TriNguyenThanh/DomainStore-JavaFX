package com.utc2.domainstore.service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.utc2.domainstore.entity.database.*;
import com.utc2.domainstore.repository.DomainRepository;
import com.utc2.domainstore.repository.PaymentHistoryRepository;
import com.utc2.domainstore.repository.TransactionInfoRepository;
import com.utc2.domainstore.repository.TransactionRepository;
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
    private final PaymentHistoryRepository paymentHistoryDAO = new PaymentHistoryRepository();
    private static VnPayService vnPayService = new VnPayService();
    public static ZaloPayService zaloPayService = new ZaloPayService();
    public static MoMoService momoService = new MoMoService();
    private static String paymentURL;
    private static boolean isRunning = false;
    private static PaymentListener listener; // thêm dòng này
    private static HttpServer server;

    public void setListener(PaymentListener l) {
        listener = l;
    }

    @Override
    public JSONObject getUserPaymentHistory(JSONObject json) {
        int userId = json.getInt("user_id");
        JSONArray jsonArray = result("user_id = " + userId);
        JSONObject result = new JSONObject();
        result.put("paymentHistory", jsonArray);
        return result;
    }

    @Override
    public JSONObject getTransactionPaymentHistory(JSONObject json) {
        String transactionId = json.getString("transaction_id");
        JSONArray jsonArray = result("transaction_id = '" + transactionId + "'");
        JSONObject result = new JSONObject();
        result.put("paymentHistory", jsonArray);
        return result;
    }

    @Override
    public JSONObject createPayment(JSONObject json) throws IOException {
        // request: total (int), transactionId (String), paymentMethod (VNPAY, MOMO, ZALOPAY) (String)
        // response: status (String) , message (String)
        JSONObject jsonObject = new JSONObject();
        String transactionId = json.getString("transactionId");
        ZaloPayService.TransactionID = transactionId;
        PaymentHistoryModel pay = PaymentHistoryRepository.getInstance().selectById(new PaymentHistoryModel(transactionId));
        TransactionModel tran = TransactionRepository.getInstance()
                .selectById(new TransactionModel(transactionId, null, null));

        if (TransactionStatusEnum.COMPLETED.equals(tran.getTransactionStatus()) || (pay != null && PaymentStatusEnum.COMPLETED.equals(pay.getPaymentStatus())))
            return response("failed", "Hoá đơn đã được thanh toán !!");

        if (!tran.getRenewal()) {
            for (TransactionInfoModel t : tran.getTransactionInfos()) {
                DomainModel domain = DomainRepository.getInstance()
                        .selectById(new DomainModel(t.getDomainId(), null, 0, null, null, 0));
                // Nếu tên miền đã có chủ sỡ hữu
                if (domain.getOwnerId() != null) {
                    String domainName = domain.getDomainName()
                            + domain.getTopLevelDomainbyId(domain.getTldId()).getTldText();
                    return response("failed", "Tên miền đã được bán " + domainName);
                }
            }
        }

        String payment = json.getString("paymentMethod");

        PaymentTypeEnum typeEnum = PaymentTypeEnum.valueOf(payment);
        if ((tran.getPaymentMethod() > 0 && tran.getPaymentMethod() < 5 ) && tran.getPaymentMethod() != typeEnum.getCode()) {
            return response("failed", "Không thể thay đổi phương thức thanh toán khi đã chọn. Vui lòng huỷ trước.");
        }

        if (isRunning) {
            // Đóng server
            server.stop(0);
            isRunning = false;
        }
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        if (payment.equals("VNPAY")) server.createContext("/vnpay", new VNPayReturnHandler());
        else if (payment.equals("ZALOPAY")) server.createContext("/zalopay", new ZaloPayReturnHandler());
        else if(payment.equals("MOMO")) server.createContext("/momo", new MoMoReturnHandler());
        else {
            jsonObject.put("status", "failed");
            jsonObject.put("message", "Không hỗ trợ phương thức thanh toán này!!");
            return jsonObject;
        }
        server.setExecutor(null); // Sử dụng executor mặc định
        // Nếu server chưa chạy, khởi động lại
        server.start();
        isRunning = true;
        System.out.println("Server đã được khởi động.");

        // Tạo transaction reference là timestamp hiện tại
        String txnRef = String.valueOf(System.currentTimeMillis());
        // Tạo URL thanh toán
        if (payment.equals("VNPAY"))
            paymentURL = vnPayService.createPaymentUrl(json.getLong("total"), transactionId, txnRef);
        else if (payment.equals("ZALOPAY"))
            paymentURL = zaloPayService.createPaymentUrl(json.getLong("total"), transactionId, txnRef);
        else {
            paymentURL = momoService.createPaymentUrl(json.getLong("total"), transactionId, txnRef);
        }

        // Cập nhật hoá đơn
        tran.setTransactionStatus(TransactionStatusEnum.PAYMENT);
        tran.setPaymentMethod(typeEnum.getCode());
        TransactionRepository.getInstance().update(tran);

        try {
            // Kiểm tra xem Desktop có được hỗ trợ không
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                URI uri = new URI(paymentURL);
                // Mở URL trong trình duyệt mặc định
                Desktop.getDesktop().browse(uri);

                System.out.println("Tạo thanh toán thành công");
                jsonObject.put("status", "success");
                jsonObject.put("message", "Tạo thanh toán thành công");
                return jsonObject;
            } else {
                System.out.println("Desktop không được hỗ trợ trên hệ thống này.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Có lỗi khi tạo thanh toán");
        return null;
    }

    public void resetPayment(String transactionId) {
        TransactionModel t = TransactionRepository.getInstance().selectById_V2(transactionId);
        t.setPaymentMethod(5);
        TransactionRepository.getInstance().update(t);
        System.out.println("Reset thanh toán");
    }

    private JSONArray result(String condition) {
        JSONArray jsonArray = new JSONArray();
        for (PaymentHistoryModel p : paymentHistoryDAO.selectByCondition(condition)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("payment_id", p.getPaymentCode());
            String transactionId = p.getTransactionId();
            jsonObject.put("transaction_id", transactionId);
            ArrayList<TransactionInfoModel> list = TransactionInfoRepository.getInstance().selectByCondition("transactions_id = '" + transactionId + "'");
            int total = 0;
            for (TransactionInfoModel tran : list) {
                total += tran.getPrice();
            }
            jsonObject.put("total", total);
            jsonObject.put("method", PaymentTypeEnum.getPaymentMethod(p.getPaymentMethodId()));
            jsonObject.put("date", p.getPaymentDate());
            jsonObject.put("status", p.getPaymentStatus());
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    private JSONObject response(String status, String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", message);
        System.out.println(message);
        return jsonObject;
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
            String response = VnPayService.createResponseHTML(paymentResult, paymentURL);
            // Gửi response về cho client
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

            server.stop(0);

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

    public static class ZaloPayReturnHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URI requestURI = exchange.getRequestURI(); // lấy phần sau dấu ?
            String query = requestURI.getQuery(); // amount=12000&appid=...

            System.out.println("Nhận callback từ ZaloPay: " + query);

            // Parse query string thành Map
            Map<String, String> parameters = VnPayUtils.parseQueryString(query);

            // Xử lý kết quả thanh toán
            Map<String, String> paymentResult = zaloPayService.processReturnUrl(parameters);

            // Tạo response HTML
            String response = ZaloPayService.createResponseHTML(paymentResult, paymentURL);
            // Gửi response về cho client
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

            server.stop(0);

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

    public static class MoMoReturnHandler implements  HttpHandler{

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URI requestURI = exchange.getRequestURI(); // lấy phần sau dấu ?
            String query = requestURI.getQuery(); // amount=12000&appid=...

            System.out.println("Nhận callback từ MoMo: " + query);

            // Parse query string thành Map
            Map<String, String> parameters = VnPayUtils.parseQueryString(query);

            // Xử lý kết quả thanh toán
            Map<String, String> paymentResult = momoService.processReturnUrl(parameters);

            // Tạo response HTML
            String response = momoService.createResponseHTML(paymentResult, paymentURL);
            // Gửi response về cho client
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

            server.stop(0);

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

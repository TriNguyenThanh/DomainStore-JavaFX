package com.utc2.domainstore.AnhDu;

import com.sun.net.httpserver.HttpServer;
import com.utc2.domainstore.service.PaymentService;
import com.utc2.domainstore.service.VnPayService;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Scanner;

public class TestApp {
    static VnPayService vnPayService = new VnPayService();

    public static void main(String[] args) throws IOException {

// ------------ PaymentService -----------
//        JSONObject json = new JSONObject();
//        json.put("user_id", 1);
//        PaymentService t = new PaymentService();
//        System.out.println(t.getUserPaymentHistory(json));
// ------------ TransactionService -----------
//        JSONObject json = new JSONObject();
//        json.put("user_id", 1);
//        json.put("transaction_id", "HD001");
//        TransactionService t = new TransactionService();
//        System.out.println(t.getAllUserTransaction(json));
//        System.out.println(t.getAllTransaction());
//        System.out.println(t.getTransactionInfomation(json));
        // ------------ PaymentHistory -----------
        // Select All
//        ArrayList<PaymentHistoryModel> payments = PaymentHistoryRepository.getInstance().selectAll();
//        for(PaymentHistoryModel p : payments)
//            System.out.println(p);
        // SelectById
//        PaymentHistoryModel p =new PaymentHistoryModel();
//        p.setPaymentId(1);
//        System.out.println(PaymentHistoryRepository.getInstance().selectById(p));
        // Insert
//        PaymentHistoryModel p =new PaymentHistoryModel("HD001", "74389326", 1, PaymentStatusEnum.FAILED, LocalDate.parse("2024-03-11"));
//        PaymentHistoryRepository.getInstance().insert(p);
        //Update
//        PaymentHistoryModel p = new PaymentHistoryModel(2, "HD001", "74389326", 1, PaymentStatusEnum.COMPLETED, LocalDate.parse("2024-03-11"));
//        PaymentHistoryRepository.getInstance().update(p);
        // Delete
//        PaymentHistoryModel p =new PaymentHistoryModel();
//        p.setPaymentId(2);
//        PaymentHistoryRepository.getInstance().delete(p);
        // ------------ Transaction -----------
        // Select All
//        ArrayList<TransactionModel> transactions = TransactionRepository.getInstance().selectAll();
//        for(TransactionModel tran : transactions){
//            System.out.println(tran);
//        }
        // SelectById
//        TransactionModel t =new TransactionModel();
//        t.setTransactionId("HD001");
//        System.out.println(TransactionRepository.getInstance().selectById(t));
        // Insert
//        TransactionModel t = new TransactionModel("HD002", 1, LocalDate.parse("2024-03-14"));
//        TransactionInfoModel ti1 = new TransactionInfoModel("HD002", 1, 123546);
//        TransactionInfoModel ti2 = new TransactionInfoModel("HD002", 2, 654321);
//        t.getTransactionInfos().add(ti1);
//        t.getTransactionInfos().add(ti2);
//        TransactionRepository.getInstance().insert(t);
        //Update
//        TransactionModel t = new TransactionModel("HD002", 1, LocalDate.parse("2024-05-03"));
//        TransactionRepository.getInstance().update(t);
        // Delete
//        TransactionModel t = new TransactionModel(); t.setTransactionId("HD002");
//        TransactionRepository.getInstance().delete(t);
        // ------------ Transaction Info -----------
        // Select All
//        ArrayList<TransactionInfoModel> listTransactionInfo = TransactionInfoRepository.getInstance().selectAll();
//        for(TransactionInfoModel t : listTransactionInfo)
//            System.out.println(t);
        // SelectById
//        TransactionInfoModel t =new TransactionInfoModel(); t.setTransactionId("HD001");
//        System.out.println(TransactionInfoRepository.getInstance().selectById(t));
        // Insert
//        TransactionInfoModel t = new TransactionInfoModel("HD001", 2, 66666);
//        TransactionInfoRepository.getInstance().insert(t);
        //Update
//        TransactionInfoModel t = new TransactionInfoModel("HD001", 2, 78000);
//        TransactionInfoRepository.getInstance().update(t);
        // Delete
//        TransactionInfoModel t = new TransactionInfoModel("HD001", 2, 78000);
//        TransactionInfoRepository.getInstance().delete(t);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/vnpay_return", new PaymentService.VNPayReturnHandler());
        server.setExecutor(null); // Sử dụng executor mặc định
        server.start();

        System.out.println("Server đang chạy trên port 8080. Đang đợi callback từ VNPay...");
        Scanner scanner = new Scanner(System.in);

        System.out.println("===== VNPAY PAYMENT DEMO =====");
        System.out.println("1. Tạo thanh toán mới");
        System.out.println("2. Thoát");
        System.out.print("Chọn chức năng: ");

        int choice = scanner.nextInt();
        // scanner.nextLine(); // Consume newline

        if (choice == 1) {
            System.out.print("Nhập số tiền (VND): ");
            int amount = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Nhập mô tả đơn hàng: ");
            String orderInfo = scanner.nextLine();

            // Tạo transaction reference là timestamp hiện tại
            String txnRef = String.valueOf(System.currentTimeMillis());

            // Tạo URL thanh toán
            String paymentUrl = vnPayService.createPaymentUrl(amount, orderInfo, txnRef);

            System.out.println("\nURL thanh toán đã được tạo:");
//            System.out.println(paymentUrl);
            try {
                // URL cần mở
                String url = paymentUrl;

                // Kiểm tra xem Desktop có được hỗ trợ không
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    // Tạo URI từ URL
                    URI uri = new URI(url);
                    // Mở URL trong trình duyệt mặc định
                    Desktop.getDesktop().browse(uri);
                } else {
                    System.out.println("Desktop không được hỗ trợ trên hệ thống này.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            System.out.println("\nMã đơn hàng của bạn: " + txnRef);
            System.out.println("\nVui lòng sử dụng URL này để thanh toán, sau đó kiểm tra kết quả trên terminal.");
            System.out.println("Server đang chạy và đợi callback từ VNPay...");
        } else {
            System.out.println("Thoát chương trình");
            System.exit(0);
        }
    }
}

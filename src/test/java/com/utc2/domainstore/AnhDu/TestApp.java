package com.utc2.domainstore.AnhDu;

import com.sun.net.httpserver.HttpServer;
import com.utc2.domainstore.entity.database.*;
import com.utc2.domainstore.repository.PaymentHistoryRepository;
import com.utc2.domainstore.repository.TransactionInfoRepository;
import com.utc2.domainstore.repository.TransactionRepository;
import com.utc2.domainstore.service.GenerateService;
import com.utc2.domainstore.service.PaymentService;
import com.utc2.domainstore.service.TransactionService;
import com.utc2.domainstore.service.VnPayService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class TestApp {
    static VnPayService vnPayService = new VnPayService();
    public static void main(String[] args) throws IOException {
        System.out.println("Chương trình test");
// ------------ PaymentService -----------
//        JSONObject json = new JSONObject();
//        json.put("user_id", 3);
//        json.put("transaction_id", "HD001");
//        PaymentService t = new PaymentService();
//        System.out.println(t.getUserPaymentHistory(json));
//        System.out.println(t.getTransactionPaymentHistory(json));
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
//        p.setTransactionId("HD001");
//        System.out.println(PaymentHistoryRepository.getInstance().selectById(p));
        // Insert
//        PaymentHistoryModel p =new PaymentHistoryModel("HD001", "74389326", 1, PaymentStatusEnum.FAILED, Timestamp.valueOf(LocalDateTime.now()));
//        PaymentHistoryRepository.getInstance().insert(p);
        //Update
//        PaymentHistoryModel p = new PaymentHistoryModel(2, "HD001", "74389326", 1, PaymentStatusEnum.COMPLETED, Timestamp.valueOf(LocalDateTime.now()));
//        PaymentHistoryRepository.getInstance().update(p);
        // Delete
//        PaymentHistoryModel p =new PaymentHistoryModel();
//        p.setPaymentId(2);
//        PaymentHistoryRepository.getInstance().delete(p);
        // ------------ Transaction -----------
        // Select All
//        ArrayList<TransactionModel> transactions = TransactionRepository.getInstance().selectAll();
//        for(TransactionModel tran : transactions)
//            System.out.println(tran);
        // SelectById
//        TransactionModel t =new TransactionModel();
//        t.setTransactionId("HD001");
//        System.out.println(TransactionRepository.getInstance().selectById(t));
        // Insert
//        TransactionModel t = new TransactionModel("HD013", 1, Timestamp.valueOf(LocalDateTime.now()));
//        TransactionInfoModel ti1 = new TransactionInfoModel("HD013", 1, 123546L);
//        TransactionInfoModel ti2 = new TransactionInfoModel("HD013", 2, 654321L);
//        t.getTransactionInfos().add(ti1);
//        t.getTransactionInfos().add(ti2);
//        TransactionRepository.getInstance().insert(t);
        //Update
//        TransactionModel t = new TransactionModel("HD013", 1, Timestamp.valueOf(LocalDateTime.now()));
//        t.setTransactionStatus(TransactionStatusEnum.COMPLETED);
//        TransactionRepository.getInstance().update(t);
        // Delete
//        TransactionModel t = new TransactionModel(); t.setTransactionId("HD013");
//        TransactionRepository.getInstance().delete(t);
        // ------------ Transaction Info -----------
        // Select All
//        ArrayList<TransactionInfoModel> listTransactionInfo = TransactionInfoRepository.getInstance().selectAll();
//        for(TransactionInfoModel t : listTransactionInfo) {
//            System.out.println(t);
//        }
//         SelectById
//        TransactionInfoModel t =new TransactionInfoModel(); t.setTransactionId("HD001");
//        System.out.println(TransactionInfoRepository.getInstance().selectById(t));
        // Insert
//        TransactionInfoModel t = new TransactionInfoModel("HD001", 2, 66666L);
//        TransactionInfoRepository.getInstance().insert(t);
        //Update
//        TransactionInfoModel t = new TransactionInfoModel("HD001", 15, 0L);
//        TransactionInfoRepository.getInstance().update(t);
        // Delete
//        TransactionInfoModel t = new TransactionInfoModel("HD001", 2, 0L);
//        TransactionInfoRepository.getInstance().delete(t);

        JSONArray domainArray = new JSONArray();
//        JSONObject domain1 = new JSONObject();
//        domain1.put("name", "globalban3.biz"); // 8
//        domain1.put("status", "AVAILABLE");
//        domain1.put("price", 299000);
//        domain1.put("years", 20);
//        domainArray.put(domain1);
        JSONObject domain2 = new JSONObject();
//        domain2.put("name", "diamonielts.com"); // 1
        domain2.put("name", "supercool.com"); // 14
        domain2.put("status", "SOLD");
        domain2.put("price", 99000);
        domain2.put("years", 30);
        domainArray.put(domain2);
//
        JSONObject domains = new JSONObject();
        domains.put("user_id", 4);
        domains.put("is_renewal", 1);
        domains.put("domains", domainArray);

        /* tạo hoá đơn
        request: domains (JSONObject)
        response: JSONObject{
                  transactionId (String),
                  total(int),
                  status ("success" / "failed")
                  }  */
//        TransactionService transactionService = new TransactionService();
//        System.out.println(transactionService.createTransaction(domains));
        /* thanh toán
        request: JSONObject {
                    total (int),
                    transactionId (String)
                 }
        response: true / false (boolean) */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("transactionId", "HD011");
        jsonObject.put("total", 2970000);
        jsonObject.put("paymentMethod", "MOMO");
//        PaymentService paymentService = new PaymentService();
//        paymentService.resetPayment("HD011");
//        System.out.println(paymentService.createPayment(jsonObject));
//        JSONObject jsonObject1 = new JSONObject();
//        jsonObject1.put("transactionId", "HD011");
//        jsonObject1.put("total", 2970000);
//        jsonObject1.put("paymentMethod", "ZALOPAY");
//        System.out.println(paymentService.createPayment(jsonObject1));
//          Tạo hoá đơn pdf
//        GenerateService generateService = new GenerateService();
//        generateService.generateInvoicePDF("HD011");
//        generateService.exportExcel("user");
//        generateService.exportExcel("domain");
    }
}

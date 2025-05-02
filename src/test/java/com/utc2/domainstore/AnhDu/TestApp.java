package com.utc2.domainstore.AnhDu;

import com.sun.net.httpserver.HttpServer;
import com.utc2.domainstore.entity.database.TransactionInfoModel;
import com.utc2.domainstore.entity.database.TransactionModel;
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
import java.util.ArrayList;
import java.util.Scanner;

public class TestApp {
    static VnPayService vnPayService = new VnPayService();
    public static void main(String[] args) throws IOException {

// ------------ PaymentService -----------
//        JSONObject json = new JSONObject();
//        json.put("user_id", 3);
//        json.put("transaction_id", "HD010");
//        PaymentService t = new PaymentService();
//        System.out.println(t.getUserPaymentHistory(json));
//        System.out.println(t.getTransactionPaymentHistory(json));
// ------------ TransactionService -----------
//        JSONObject json = new JSONObject();
//        json.put("user_id", 6);
//        json.put("transaction_id", "HD012");
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
//        for(TransactionInfoModel t : listTransactionInfo) {
//            System.out.println(t);
//        }
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

        JSONArray domainArray = new JSONArray();
        JSONObject domain1 = new JSONObject();
        domain1.put("name", "diamonielts.com");
        domain1.put("status", "available");
        domain1.put("price", 299000);
        domain1.put("years", 2);
        domainArray.put(domain1);
        JSONObject domain2 = new JSONObject();
        domain2.put("name", "globalban3.biz");
        domain2.put("status", "available");
        domain2.put("price", 99000);
        domain2.put("years", 3);
        domainArray.put(domain2);

        JSONObject domains = new JSONObject();
        domains.put("user_id", 6);
        domains.put("domains", domainArray);

        /* tạo hoá đơn
        request: domains (JSONObject)
        response: JSONObject{
                  transactionId (String),
                  total(int),
                  status ("success" / "failed")
                  }  */
        TransactionService transactionService = new TransactionService();
        JSONObject jsonObject = transactionService.createTransaction(domains);
        /* thanh toán
        request: JSONObject {
                    total (int),
                    transactionId (String)
                 }
        response: true / false (boolean) */
        PaymentService paymentService = new PaymentService();
        paymentService.createPayment(jsonObject);
//          Tạo hoá đơn pdf
//        GenerateService generateService = new GenerateService();
//        generateService.generateInvoicePDF("HD005");
    }
}

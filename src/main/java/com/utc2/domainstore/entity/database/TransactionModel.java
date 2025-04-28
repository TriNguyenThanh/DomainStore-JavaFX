
package com.utc2.domainstore.entity.database;

import java.time.LocalDate;
import java.util.ArrayList;

public class TransactionModel {
    private String transactionId; 
    private Integer userId; // 
    private LocalDate transactionDate; // chuyển LocalDate thành Date trước khi lưu vào database
    private TransactionStatusEnum transactionStatus;
    private PaymentStatusEnum paymentStatus;
    
    private Integer totalCost;
    private final ArrayList<TransactionInfoModel> transactionInfos = new ArrayList<>();

    public TransactionModel() {
    }

    public TransactionModel(String transactionId, Integer userId, LocalDate transactionDate) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.transactionDate = transactionDate;
    }

    public TransactionModel(String transactionId, Integer userId, LocalDate transactionDate, PaymentStatusEnum paymentStatus) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.transactionDate = transactionDate;
        this.paymentStatus = paymentStatus;
    }

    public TransactionModel(String transactionId, Integer userId, LocalDate transactionDate, PaymentStatusEnum paymentStatus, Integer totalCost) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.transactionDate = transactionDate;
        this.paymentStatus = paymentStatus;
        this.totalCost = totalCost;
    }
    
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionStatusEnum getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatusEnum transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public PaymentStatusEnum getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Integer getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Integer totalCost) {
        this.totalCost = totalCost;
    }

    public ArrayList<TransactionInfoModel> getTransactionInfos() {
        return transactionInfos;
    }

    @Override
    public String toString() {
        return "TransactionModel{" + "transactionId=" + transactionId + ", userId=" + userId + ", transactionDate=" + transactionDate + ", paymentStatus=" + paymentStatus + ", totalCost=" + totalCost + ", transactionInfos=" + transactionInfos + '}';
    }
    
}

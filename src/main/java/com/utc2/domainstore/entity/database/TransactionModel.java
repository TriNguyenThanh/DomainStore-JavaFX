
package com.utc2.domainstore.entity.database;

import java.sql.Timestamp;
import java.util.ArrayList;

public class TransactionModel {
    private String transactionId; 
    private Integer userId; // 
    private Timestamp transactionDate;
    private TransactionStatusEnum transactionStatus;
    private PaymentStatusEnum paymentStatus;
    
    private Long totalCost;
    private final ArrayList<TransactionInfoModel> transactionInfos = new ArrayList<>();

    public TransactionModel() {
    }

    public TransactionModel(String transactionId, Integer userId, Timestamp transactionDate) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.transactionDate = transactionDate;
    }

    public TransactionModel(String transactionId, Integer userId, Timestamp transactionDate, PaymentStatusEnum paymentStatus) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.transactionDate = transactionDate;
        this.paymentStatus = paymentStatus;
    }

    public TransactionModel(String transactionId, Integer userId, Timestamp transactionDate, PaymentStatusEnum paymentStatus, Long totalCost) {
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

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
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

    public Long getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Long totalCost) {
        this.totalCost = totalCost;
    }

    public ArrayList<TransactionInfoModel> getTransactionInfos() {
        return transactionInfos;
    }

    @Override
    public String toString() {
        return "TransactionModel{" +
                "transactionId='" + transactionId + '\'' +
                ", userId=" + userId +
                ", transactionDate=" + transactionDate +
                ", transactionStatus=" + transactionStatus +
                ", paymentStatus=" + paymentStatus +
                ", totalCost=" + totalCost +
                ", transactionInfos=" + transactionInfos +
                '}';
    }
}

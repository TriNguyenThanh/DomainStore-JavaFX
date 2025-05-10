
package com.utc2.domainstore.entity.database;

import java.sql.Timestamp;

public class PaymentHistoryModel {
    private Integer paymentId;
    private String transactionId;
    private String paymentCode; // mã giao dịch bên thứ 3
    private Integer paymentMethodId; 
    private PaymentStatusEnum paymentStatus;
    private Timestamp paymentDate;

    public PaymentHistoryModel() {
    }

    public PaymentHistoryModel(String transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentHistoryModel(Integer paymentId, String transactionId, String paymentCode, Integer paymentMethodId, PaymentStatusEnum paymentStatus, Timestamp paymentDate) {
        this.paymentId = paymentId;
        this.transactionId = transactionId;
        this.paymentCode = paymentCode;
        this.paymentMethodId = paymentMethodId;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
    }

    public PaymentHistoryModel(String transactionId, String paymentCode, Integer paymentMethodId, PaymentStatusEnum paymentStatus, Timestamp paymentDate) {
        this.transactionId = transactionId;
        this.paymentCode = paymentCode;
        this.paymentMethodId = paymentMethodId;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public Integer getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(Integer paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public PaymentStatusEnum getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    @Override
    public String toString() {
        return "PaymentHistoryModel{" + "paymentId=" + paymentId + ", transactionId=" + transactionId + ", paymentCode=" + paymentCode + ", paymentMethodId=" + paymentMethodId + ", paymentStatus=" + paymentStatus + ", paymentDate=" + paymentDate + '}';
    }
    
}

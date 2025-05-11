package com.utc2.domainstore.entity.view;

import java.time.LocalDateTime;

public class PaymentViewModel {
    private String billID;
    private String paymentID;
    private STATUS status;
    private LocalDateTime paymentDate;
    private String method;
    private Integer price;

    public PaymentViewModel() {
    }

    public PaymentViewModel(String billID, String paymentID, String method, STATUS status, LocalDateTime paymentDate) {
        this.billID = billID;
        this.paymentID = paymentID;
        this.status = status;
        this.paymentDate = paymentDate;
        this.method = method;
    }

    public PaymentViewModel(String billID, String paymentID, String method, STATUS status, LocalDateTime paymentDate, Integer price) {
        this.billID = billID;
        this.paymentID = paymentID;
        this.status = status;
        this.paymentDate = paymentDate;
        this.method = method;
        this.price = price;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentDateString() {
        return String.format("%02d/02d/04d", paymentDate.getDayOfMonth(), paymentDate.getMonthValue(), paymentDate.getYear());
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "PaymentViewModel{" +
                "billID='" + billID + '\'' +
                ", paymentID='" + paymentID + '\'' +
                ", status=" + status +
                ", paymentDate=" + getPaymentDateString() +
                ", method='" + method + '\'' +
                '}';
    }
}

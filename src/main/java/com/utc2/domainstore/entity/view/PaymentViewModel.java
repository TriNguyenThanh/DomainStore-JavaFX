package com.utc2.domainstore.entity.view;

import java.time.LocalDate;

public class PaymentViewModel {
    private String billID;
    private int paymentID;
    private STATUS status;
    private LocalDate paymentDate;
    private String method;

    public PaymentViewModel() {
    }

    public PaymentViewModel(String billID, int paymentID, String method, STATUS status, LocalDate paymentDate) {
        this.billID = billID;
        this.paymentID = paymentID;
        this.status = status;
        this.paymentDate = paymentDate;
        this.method = method;
    }

    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }

    public int getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(int paymentID) {
        this.paymentID = paymentID;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentDateString() {
        return String.format("%02d/02d/04d", paymentDate.getDayOfMonth(), paymentDate.getMonthValue(), paymentDate.getYear());
    }

    public void setPaymentDate(LocalDate paymentDate) {
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

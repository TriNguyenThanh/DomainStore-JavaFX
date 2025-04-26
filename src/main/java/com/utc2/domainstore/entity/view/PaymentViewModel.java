package com.utc2.domainstore.entity.view;

import java.sql.Date;

public class PaymentViewModel {
    private String billID;
    private String paymentID;
    private STATUS status;
    private Date paymentDate;

    public PaymentViewModel() {
    }

    public PaymentViewModel(String billID, String paymentID, STATUS status, Date paymentDate) {
        this.billID = billID;
        this.paymentID = paymentID;
        this.status = status;
        this.paymentDate = paymentDate;
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

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
}

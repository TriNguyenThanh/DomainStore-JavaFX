package com.utc2.domainstore.entity.view;

import java.sql.Date;

public class BillViewModel {
    private String id;
    private Date date;
    private STATUS status;
    private int price;

    public BillViewModel(String id, Date date, STATUS status, int price) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.price = price;
    }

    public BillViewModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}

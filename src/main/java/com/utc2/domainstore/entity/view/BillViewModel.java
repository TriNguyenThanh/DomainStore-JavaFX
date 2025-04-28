package com.utc2.domainstore.entity.view;

import java.time.LocalDate;

public class BillViewModel {
    private String id;
    private LocalDate date;
    private STATUS status;
    private int price;

    public BillViewModel(String id, LocalDate date, STATUS status, int price) {
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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

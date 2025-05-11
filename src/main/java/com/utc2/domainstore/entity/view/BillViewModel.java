package com.utc2.domainstore.entity.view;

import java.time.LocalDateTime;

public class BillViewModel {
    private String id;
    private LocalDateTime date;
    private STATUS status;
    private Integer price;
    private Integer userId;

    public BillViewModel(String id, LocalDateTime date, STATUS status, Integer price, Integer userId) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.price = price;
        this.userId = userId;
    }

    public BillViewModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}

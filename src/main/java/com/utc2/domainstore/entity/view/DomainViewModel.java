package com.utc2.domainstore.entity.view;

public class DomainViewModel {
    private String name;
    private STATUS status;
    private int price;
    private int years;

    public DomainViewModel() {

    }

    public DomainViewModel(String name, STATUS status, int price, int years) {
        this.name = name;
        this.status = status;
        this.price = price;
        this.years = years;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getYears() {
        return years;
    }

    public void setYears(int years) {
        this.years = years;
    }
}

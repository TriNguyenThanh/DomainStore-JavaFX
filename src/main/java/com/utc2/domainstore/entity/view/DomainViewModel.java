package com.utc2.domainstore.entity.view;

import java.time.LocalDate;

public class DomainViewModel {
    private String name;
    private STATUS status;
    private Integer price;
    private Integer years;
    private LocalDate date;
    private Integer OwnerId;

    public DomainViewModel() {

    }

    public DomainViewModel(String name, STATUS status, int price, int years, LocalDate date) {
        this.name = name;
        this.status = status;
        this.price = price;
        this.years = years;
    }

    public DomainViewModel(String name, STATUS status, Integer price, Integer years, LocalDate date, Integer ownerId) {
        this.name = name;
        this.status = status;
        this.price = price;
        this.years = years;
        this.date = date;
        this.OwnerId = ownerId;
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getYears() {
        return years;
    }

    public void setYears(Integer years) {
        this.years = years;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getOwnerId() {
        return OwnerId;
    }

    public void setOwnerId(Integer ownerId) {
        OwnerId = ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainViewModel that = (DomainViewModel) o;
        return name.equals(that.name); // Compare based on the 'name' field
    }

    @Override
    public int hashCode() {
        return name.hashCode(); // Use the 'name' field for hash code
    }
}

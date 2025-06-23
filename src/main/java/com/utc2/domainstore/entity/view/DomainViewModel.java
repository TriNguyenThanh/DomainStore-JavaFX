package com.utc2.domainstore.entity.view;

import java.time.LocalDateTime;

public class DomainViewModel {
    private String name;
    private STATUS status;
    private Integer price;
    private Integer years;
    private LocalDateTime date;
    private Integer ownerID;
    private String ownerName;

    public DomainViewModel() {

    }

    public DomainViewModel(String name, STATUS status, Integer price, Integer years, LocalDateTime date) {
        this.name = name;
        this.status = status;
        this.price = price;
        this.years = years;
        this.date = date;
    }

    public DomainViewModel(String name, STATUS status, Integer price, Integer years, LocalDateTime date, Integer ownerID, String ownerName) {
        this.name = name;
        this.status = status;
        this.price = price;
        this.years = years;
        this.date = date;
        this.ownerID = ownerID;
        this.ownerName = ownerName;

        if (this.status == STATUS.SOLD && ownerID == null) this.status = STATUS.PAYMENT;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(Integer ownerID) {
        this.ownerID = ownerID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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

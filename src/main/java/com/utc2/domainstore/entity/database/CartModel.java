package com.utc2.domainstore.entity.database;

import java.sql.Timestamp;

public class CartModel {
    private int id;
    private int cus_id;
    private int domain_id;
    private int years;
    private Timestamp createdAt;

    public CartModel() {
    }

    public CartModel(int id, int cus_id, int domain_id, int years) {
        this.id = id;
        this.cus_id = cus_id;
        this.domain_id = domain_id;
        this.years = years;
    }
    
    public CartModel(int cus_id, int domain_id, int years) {
        this.cus_id = cus_id;
        this.domain_id = domain_id;
        this.years = years;
    }
    
    public CartModel(int id, int cus_id, int domain_id, int years, Timestamp createdAt) {
        this.id = id;
        this.cus_id = cus_id;
        this.domain_id = domain_id;
        this.years = years;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getCus_id() {
        return cus_id;
    }

    public int getDomain_id() {
        return domain_id;
    }

    public int getYears() {
        return years;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCus_id(int cus_id) {
        this.cus_id = cus_id;
    }

    public void setDomain_id(int domain_id) {
        this.domain_id = domain_id;
    }

    public void setYears(int years) {
        this.years = years;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "CartModel{" + "id=" + id + ", cus_id=" + cus_id + ", domain_id=" + domain_id + ", years=" + years + ", createdAt=" + createdAt + '}';
    }
}

package com.utc2.domainstore.entity.database;

public class TopLevelDomainModel {
    private int id;
    private String tldText;
    private int price;

    public TopLevelDomainModel() {
    }

    public TopLevelDomainModel(int id) {
        this.id = id;
    }
    
    public TopLevelDomainModel(int id, String tldText, int price) {
        this.id = id;
        this.tldText = tldText;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTldText() {
        return tldText;
    }

    public void setTldText(String tldText) {
        this.tldText = tldText;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    
    @Override
    public String toString() {
        return "TopLevelDomainModel{" +
                "id=" + id +
                ", tldText='" + tldText + '\'' +
                ", price=" + price +
                '}';
    }
}

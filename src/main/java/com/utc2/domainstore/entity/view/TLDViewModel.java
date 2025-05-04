package com.utc2.domainstore.entity.view;

public class TLDViewModel {
    private Integer ID;
    private String name;
    private Integer price;

    public TLDViewModel() {
    }

    public TLDViewModel(Integer ID, String name, Integer price) {
        this.ID = ID;
        this.name = name;
        this.price = price;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

}

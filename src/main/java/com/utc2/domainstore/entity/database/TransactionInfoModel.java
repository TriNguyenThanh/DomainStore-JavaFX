
package com.utc2.domainstore.entity.database;

public class TransactionInfoModel {
    private String transactionId;
    private Integer domainId;
    private Integer price;

    public TransactionInfoModel() {
    }

    public TransactionInfoModel(String transactionId, Integer domainId, Integer price) {
        this.transactionId = transactionId;
        this.domainId = domainId;
        this.price = price;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getDomainId() {
        return domainId;
    }

    public void setDomainId(Integer domainId) {
        this.domainId = domainId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "TransactionInfoModel{" + "transactionId=" + transactionId + ", domainId=" + domainId + ", price=" + price + '}';
    }

}

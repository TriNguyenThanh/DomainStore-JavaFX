
package com.utc2.domainstore.entity.database;

public class TransactionInfoModel {
    private String transactionId;
    private Integer domainId;
    private Long price;
    private Integer years;

    public TransactionInfoModel() {
    }

    public TransactionInfoModel(String transactionId, Integer domainId, Long price) {
        this.transactionId = transactionId;
        this.domainId = domainId;
        this.price = price;
    }

    public TransactionInfoModel(String transactionId, Integer domainId, Long price, Integer years) {
        this.transactionId = transactionId;
        this.domainId = domainId;
        this.price = price;
        this.years = years;
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

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Integer getYears() {
        return years;
    }

    public void setYears(Integer years) {
        this.years = years;
    }

    @Override
    public String toString() {
        return "TransactionInfoModel{" +
                "transactionId='" + transactionId + '\'' +
                ", domainId=" + domainId +
                ", price=" + price +
                ", years=" + years +
                '}';
    }
}

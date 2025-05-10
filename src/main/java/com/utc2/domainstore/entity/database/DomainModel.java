package com.utc2.domainstore.entity.database;

import com.utc2.domainstore.repository.TopLevelDomainRepository;

import java.sql.Date;

public class DomainModel {
    private int id;
    private String domainName;
    private int tldId;
    private DomainStatusEnum status;
    private Date activeDate;
    private int years;
    private int price;
    private Integer ownerId;
    private Date createdAt; 
    
    public DomainModel() {
    }

    public DomainModel(int id, String domainName, int tldId, DomainStatusEnum status, Date activeDate, int years, int price, Integer ownerId, Date createdAt) {
        this.id = id;
        this.domainName = domainName;
        this.tldId = tldId;
        this.status = status;
        this.activeDate = activeDate;
        this.years = years;
        this.price = price;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
    }

    public DomainModel(int id, String domainName, int tldId, DomainStatusEnum status, Date activeDate, int years, int price, Integer ownerId) {
        this.id = id;
        this.domainName = domainName;
        this.tldId = tldId;
        this.status = status;
        this.activeDate = activeDate;
        this.years = years;
        this.price = price;
        this.ownerId = ownerId;
    }
    
    public DomainModel(int id, String domainName, int tldId, DomainStatusEnum status, Date activeDate, int years, Integer ownerId) {
        this.id = id;
        this.domainName = domainName;
        this.tldId = tldId;
        this.status = status;
        this.activeDate = activeDate;
        this.years = years;
        this.ownerId = ownerId;
    }
    public DomainModel(int id, String domainName, int tldId, DomainStatusEnum status, Date activeDate, int years, Integer ownerId, Date createdAt) {
        this.id = id;
        this.domainName = domainName;
        this.tldId = tldId;
        this.status = status;
        this.activeDate = activeDate;
        this.years = years;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
    }
    public DomainModel(int id, String domainName, int tldId, DomainStatusEnum status, Date activeDate, int years) {
        this.id = id;
        this.domainName = domainName;
        this.tldId = tldId;
        this.status = status;
        this.activeDate = activeDate;
        this.years = years;
    }
    public DomainModel(String domainName, int tldId, DomainStatusEnum status, int years) {
        this.domainName = domainName;
        this.tldId = tldId;
        this.status = status;
        this.years = years;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public int getTldId() {
        return tldId;
    }

    public void setTldId(int tldId) {
        this.tldId = tldId;
    }

    public DomainStatusEnum getStatus() {
        return status;
    }

    public void setStatus(DomainStatusEnum status) {
        this.status = status;
    }

    public Date getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(Date activeDate) {
        this.activeDate = activeDate;
    }

    public int getYears() {
        return years;
    }

    public void setYears(int years) {
        this.years = years;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    public TopLevelDomainModel getTopLevelDomainbyId(int id){
        return TopLevelDomainRepository.getInstance().selectById(new TopLevelDomainModel(id));
    }
    @Override
    public String toString() {
        return "DomainModel{" +
                "id=" + id +
                ", domainName='" + domainName + '\'' +
                ", tldId=" + tldId +
                ", status=" + status +
                ", activeDate=" + activeDate +
                ", years=" + years +
                ", ownerId=" + ownerId +
                ", createdAt=" + createdAt +
                '}';
    }
}

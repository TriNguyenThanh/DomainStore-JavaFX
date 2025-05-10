package com.utc2.domainstore.entity.database;

public class DomainWithTldModel {
    private DomainModel domain;
    private TopLevelDomainModel tld;

    public DomainWithTldModel() {
    }

    public DomainWithTldModel(DomainModel domain, TopLevelDomainModel tld) {
        this.domain = domain;
        this.tld = tld;
    }

    public DomainModel getDomain() {
        return domain;
    }

    public TopLevelDomainModel getTld() {
        return tld;
    }

    public void setDomain(DomainModel domain) {
        this.domain = domain;
    }

    public void setTld(TopLevelDomainModel tld) {
        this.tld = tld;
    }

    @Override
    public String toString() {
        return "DomainWithTldModel{" + "domain=" + domain + ", tld=" + tld + '}';
    }
    
    
}

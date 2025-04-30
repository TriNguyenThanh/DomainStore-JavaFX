package com.utc2.domainstore.DuongTan;

import com.utc2.domainstore.service.DomainNotifierServices;
import com.utc2.domainstore.service.SoldDomainNotifierServices;

public class testSendEmail {
    public static void main(String[] args) {
//        DomainNotifierServices a = new DomainNotifierServices();
//        a.notifyExpiringDomains();
        
        SoldDomainNotifierServices b = new SoldDomainNotifierServices();
        b.notifySoldDomain("auduongtan321@gmail.com", "newproject");
    }
}

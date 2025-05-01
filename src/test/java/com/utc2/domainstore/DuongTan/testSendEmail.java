package com.utc2.domainstore.DuongTan;

import com.utc2.domainstore.service.DomainNotifierServices;
import com.utc2.domainstore.service.SoldDomainNotifierServices;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class testSendEmail {
    public static void main(String[] args) {
        //test gui email cho nhung nguoi co domain sap het han
//        JSONArray jsonInput = new JSONArray();
//
//        JSONObject domain1 = new JSONObject();
//        domain1.put("email", "auduongtan321@gmail.com");
//        domain1.put("domain_name", "myshop.com");
//        domain1.put("expired_date", "2025-05-05");
//        jsonInput.put(domain1);
//
//        JSONObject domain2 = new JSONObject();
//        domain2.put("email", "auduongtan321@gmail.com");
//        domain2.put("domain_name", "myblog.net");
//        domain2.put("expired_date", "2025-05-07");
//        jsonInput.put(domain2);
//
//        JSONObject domain3 = new JSONObject();
//        domain3.put("email", "dangxuanchat000"); // sẽ tự thêm @gmail.com
//        domain3.put("domain_name", "coolsite.org");
//        domain3.put("expired_date", "2025-05-06");
//        jsonInput.put(domain3);
//
//        DomainNotifierServices service = new DomainNotifierServices();
//        JSONObject result = service.notifyExpiringDomains(jsonInput);
//
//        System.out.println(result.toString(2));

        //test lay thong tin email nhung nguoi co domain sap het han
        DomainNotifierServices a = new DomainNotifierServices();
        JSONObject result = a.getExpiringDomainsAsJson();
        System.out.println(result.toString(2));

        //test gui email thanh toan thanh cong
//        List<String> domains = Arrays.asList("example1.com", "example2.net", "example3.org");
//        new SoldDomainNotifierServices().notifySoldDomains("auduongtan321@gmail.com", domains);
    }
}

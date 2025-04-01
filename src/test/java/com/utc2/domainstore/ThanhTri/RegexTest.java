package com.utc2.domainstore.ThanhTri;

import com.utc2.domainstore.service.DomainServices;
import com.utc2.domainstore.service.IDomain;
import org.json.JSONObject;

public class RegexTest {

    public static void main(String[] args) {
        JSONObject request = new JSONObject();
        String domainName = "example.com";
        request.put("name", domainName);

        IDomain domainService = new DomainServices();
        JSONObject response = domainService.search(request);
        System.out.println(response.toString());
    }
}

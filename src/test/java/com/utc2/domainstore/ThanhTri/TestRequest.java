package com.utc2.domainstore.ThanhTri;

import com.utc2.domainstore.service.AccountServices;
import com.utc2.domainstore.service.IAccount;
import org.json.JSONObject;

public class TestRequest {
    private static void testAccount() {
        JSONObject request = new JSONObject();
        request.put("user_id", 1);

        IAccount accountService = new AccountServices();

        JSONObject respondAllAccount = accountService.getAllUserAccount();
        JSONObject respondAccount = accountService.getUserInformation(request);

        System.out.println("Done");
    }

    public static void main(String[] args) {
        testAccount();
    }
}

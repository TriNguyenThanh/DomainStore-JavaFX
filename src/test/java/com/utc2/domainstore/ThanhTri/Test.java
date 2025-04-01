package com.utc2.domainstore.ThanhTri;


import com.utc2.domainstore.service.AccountServices;
import com.utc2.domainstore.view.UserSession;
import org.json.JSONObject;

public class Test {
    public static void main(String[] args) {
        UserSession.getInstance().setUserId(1);
        getData();
    }

    private static void getData() {
        JSONObject request = new JSONObject();
        request.put("user_id", UserSession.getInstance().getUserId());

        AccountServices accountServices = new AccountServices();
        JSONObject respond = accountServices.getUserInformation(request);

        System.out.println(respond);

        String fullname = respond.getString("username");
        String phone = respond.getString("phone");
        String email = respond.getString("email");
        String psID = respond.getString("personal_id");
        String pass = respond.getString("password");

        System.out.println(fullname);
        System.out.println(phone);
        System.out.println(email);
        System.out.println(psID);
        System.out.println(pass);
    }
}
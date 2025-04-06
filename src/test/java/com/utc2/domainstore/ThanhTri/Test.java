package com.utc2.domainstore.ThanhTri;


import com.utc2.domainstore.entity.view.BillViewModel;
import com.utc2.domainstore.service.ITransactionService;
import com.utc2.domainstore.service.TransactionService;
import com.utc2.domainstore.view.UserSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        try {
            UserSession.getInstance().setUserId(2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        getData();
    }

    private static void getData() {
        List<BillViewModel> bills = new ArrayList<>();

        JSONObject request = new JSONObject();
        request.put("user_id", UserSession.getInstance().getUserId());

        ITransactionService transactionService = new TransactionService();
        JSONObject respond = transactionService.getAllTransaction();
        JSONArray list = respond.getJSONArray("transactions");

        for (Object o : list) {
            JSONObject jsonObject = (JSONObject) o;
            String id = jsonObject.getString("id");
            Date date = Date.valueOf(jsonObject.get("date").toString()); //jsonObject.get("date");
            String status = String.valueOf(jsonObject.get("status"));
            int price = jsonObject.getInt("total_price");

            System.out.println();
            System.out.println("ID: " + id);
            System.out.println("Date: " + date);
            System.out.println("Status: " + status);
            System.out.println("Price: " + price);
        }
    }
}
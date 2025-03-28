package com.utc2.domainstore.service;

import com.utc2.domainstore.dao.DomainDAO;
import com.utc2.domainstore.dao.TransactionDAO;
import com.utc2.domainstore.dao.TransactionInfoDAO;
import com.utc2.domainstore.entity.database.DomainModel;
import com.utc2.domainstore.entity.database.TransactionInfoModel;
import com.utc2.domainstore.entity.database.TransactionModel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TransactionService {

    private final ArrayList<TransactionModel> transactions = TransactionDAO.getInstance().selectAll();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final TransactionInfoDAO transactionInfoDAO = new TransactionInfoDAO();

    public JSONObject getAllTransaction() {
        JSONArray jsonArray = new JSONArray();
        for (TransactionModel t : transactionDAO.selectAll()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", t.getTransactionId());
            jsonObject.put("date", t.getTransactionDate());
            jsonObject.put("status", t.getTransactionStatus());
            jsonObject.put("total_price", t.getTotalCost());
            jsonArray.put(jsonObject);
        }
        JSONObject result = new JSONObject();
        result.put("transactions", jsonArray);
        return result;
    }
    
    public JSONObject getAllUserTransaction(JSONObject json) {
        int userId = json.getInt("user_id");
        JSONArray jsonArray = new JSONArray();
        for (TransactionModel t : transactionDAO.selectByCondition("user_id = " + userId)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", t.getTransactionId());
            jsonObject.put("date", t.getTransactionDate());
            jsonObject.put("status", t.getTransactionStatus());
            jsonObject.put("total_price", t.getTotalCost());
            jsonArray.put(jsonObject);
        }
        JSONObject result = new JSONObject();
        result.put("transactions", jsonArray);
        return result;
    }
    
    public JSONObject getTransactionInfomation(JSONObject json) {
        String transactionId = json.getString("transaction_id");
        JSONArray jsonArray = new JSONArray();
        TransactionModel t = new TransactionModel(); t.setTransactionId(transactionId);
        for (TransactionInfoModel ti : transactionDAO.selectById(t).getTransactionInfos()) {
            JSONObject jsonObject = new JSONObject();
            DomainModel d = new DomainModel(); d.setId(ti.getDomainId());
            DomainModel domain = DomainDAO.getInstance().selectById(d);
            jsonObject.put("name", domain.getDomainName());
            jsonObject.put("status", domain.getStatus());
            jsonObject.put("price", domain.getTopLevelDomainbyId(domain.getTldId()).getPrice());
            jsonObject.put("years", domain.getYears());
            jsonArray.put(jsonObject);
        }
        JSONObject result = new JSONObject();
        result.put("domains", jsonArray);
        return result;
    }
}

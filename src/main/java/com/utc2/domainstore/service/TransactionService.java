package com.utc2.domainstore.service;

import com.utc2.domainstore.entity.database.DomainModel;
import com.utc2.domainstore.entity.database.TransactionInfoModel;
import com.utc2.domainstore.entity.database.TransactionModel;
import com.utc2.domainstore.repository.DomainRepository;
import com.utc2.domainstore.repository.TransactionInfoRepository;
import com.utc2.domainstore.repository.TransactionRepository;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TransactionService implements ITransactionService {

    private final ArrayList<TransactionModel> transactions = TransactionRepository.getInstance().selectAll();
    private final TransactionRepository transactionDAO = new TransactionRepository();
    private final TransactionInfoRepository transactionInfoDAO = new TransactionInfoRepository();

    @Override
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

    @Override
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

    @Override
    public JSONObject getTransactionInfomation(JSONObject json) {
        String transactionId = json.getString("transaction_id");
        JSONArray jsonArray = new JSONArray();
        TransactionModel t = new TransactionModel();
        t.setTransactionId(transactionId);
        for (TransactionInfoModel ti : transactionDAO.selectById(t).getTransactionInfos()) {
            JSONObject jsonObject = new JSONObject();
            DomainModel d = new DomainModel();
            d.setId(ti.getDomainId());
            DomainModel domain = DomainRepository.getInstance().selectById(d);
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

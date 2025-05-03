package com.utc2.domainstore.service;

import com.utc2.domainstore.entity.database.*;
import com.utc2.domainstore.repository.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class TransactionService implements ITransactionService {

    private final ArrayList<TransactionModel> transactions = TransactionRepository.getInstance().selectAll_V3();
    private final TransactionRepository transactionRepository = new TransactionRepository();
    private final TransactionInfoRepository transactionInfoRepository = new TransactionInfoRepository();
    private static JSONArray jsonArray;
    private static String transactionId;
    private static JSONObject jsonObject;
    @Override
    public JSONObject getAllTransaction() {
        JSONArray jsonArray = new JSONArray();
        for (TransactionModel t : transactionRepository.selectAll()) {
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
        for (TransactionModel t : transactionRepository.selectByCondition("user_id = " + userId)) {
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
        for (TransactionInfoModel ti : transactionRepository.selectById(t).getTransactionInfos()) {
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
    @Override
    public JSONObject createTransaction(JSONObject json) throws IOException {
        // request: domains (JSONObject)
        // response: transactionId (String), total(int), status (success / failed)

        if(json.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", "failed");
            return jsonObject;
        }
        TransactionModel tran = new TransactionModel();
        transactionId = generateTransactionId();
        tran.setTransactionId(transactionId);
        tran.setUserId(json.getInt("user_id")); // request
        tran.setTransactionDate(LocalDate.now());
        transactionRepository.insert(tran);
        jsonObject = json;
        jsonArray = json.getJSONArray("domains"); // request
        int total = processTransactionDetails(transactionId, jsonArray);
        JSONObject response = new JSONObject();
        response.put("transactionId", transactionId);
        response.put("total", total);
        response.put("status", "success");
        return  response;
    }
    @Override
    public void updateTransactionStatus(String transactionId, TransactionStatusEnum status){
        List<String> domains = new ArrayList<>();
        TransactionModel tran = transactionRepository.selectById_V2(transactionId);
        CustomerModel cus = CustomerRepository.getInstance().selectById(new CustomerModel(jsonObject.getInt("user_id")));
        tran.setTransactionStatus(status);
        if(TransactionStatusEnum.COMPLETED.equals(status)){
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                int domainId = getDomainByName(json.getString("name"));
                DomainModel d = new DomainModel(); d.setId(domainId);
                DomainModel domain = DomainRepository.getInstance().selectById(d);
                // Thêm tên miền vào List<String>
                domains.add(domain.getDomainName()
                        + domain.getTopLevelDomainbyId(domain.getTldId()).getTldText());
                // Set các giá trị để update domain
                int price = json.getInt("price");
                int years = json.getInt("years");
                domain.setYears(years);
                domain.setStatus(DomainStatusEnum.sold);
                domain.setActiveDate(Date.valueOf(LocalDate.now()));
                domain.setOwnerId(cus.getId());

                // update domain
                DomainRepository.getInstance().update(domain);
                // thêm chi tiết hoá đơn
                transactionInfoRepository.insert(new TransactionInfoModel(transactionId, domainId, price));
            }
            // gửi thông báo email
            SoldDomainNotifierServices notifier = new SoldDomainNotifierServices();
//        notifier.notifySoldDomains(cus.getEmail(),domains);
            notifier.notifySoldDomains("tringuyenntt1505@gmail.com",domains);
            transactionRepository.update(tran);
        }
    }
    //Tạo transactionId
    private String generateTransactionId(){
        if(transactions.isEmpty()) return "HD001";
        String lastId = transactions.getLast().getTransactionId();
        int number = Integer.parseInt(lastId.substring(2)); ++number;
        return String.format("HD%03d", number);
    }
    // Lấy domain_id
    private int getDomainByName(String name){
        int index = name.indexOf('.');
        String domainName = name.substring(0, index);
        String tldText = name.substring(index);
        TopLevelDomainModel tld = TopLevelDomainRepository.getInstance().getTLDByName(tldText);
        DomainModel d = DomainRepository.getInstance().getDomainByNameAndTld(name, tld.getId());
        if (d != null) {
            return d.getId();
        } else {
            throw new NoSuchElementException("Domain not found!");
        }
    }
    private int processTransactionDetails(String transactionId, JSONArray jsonArray) {
        int total = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int domainId = getDomainByName(jsonObject.getString("name"));
            int price = jsonObject.getInt("price") * jsonObject.getInt("years");
            total += price;
            transactionInfoRepository.insert(new TransactionInfoModel(transactionId, domainId, price));
        }
        return total;
    }
}

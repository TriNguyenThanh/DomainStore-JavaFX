
package com.utc2.domainstore.service;

import com.utc2.domainstore.repository.PaymentHistoryRepository;
import com.utc2.domainstore.entity.database.PaymentHistoryModel;
import com.utc2.domainstore.entity.database.PaymentTypeEnum;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PaymentService implements  IPaymentService{
    private ArrayList<PaymentHistoryModel> listPaymentHistory = PaymentHistoryRepository.getInstance().selectAll();
    private final PaymentHistoryRepository paymentHistoryDAO = new PaymentHistoryRepository();

    @Override
    public JSONObject getUserPaymentHistory(JSONObject json){
        int userId = json.getInt("user_id");
        JSONArray jsonArray = new JSONArray();
        for (PaymentHistoryModel p : paymentHistoryDAO.selectByCondition("user_id = " + userId)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("payment_id", p.getPaymentId());
            jsonObject.put("transaction_id", p.getTransactionId());
            jsonObject.put("method", PaymentTypeEnum.getPaymentMethod(p.getPaymentMethodId()));
            jsonObject.put("date", p.getPaymentDate());
            jsonObject.put("status", p.getPaymentStatus());
            jsonArray.put(jsonObject);
        }
        JSONObject result = new JSONObject();
        result.put("paymentHistory", jsonArray);
        return result;
    }
}

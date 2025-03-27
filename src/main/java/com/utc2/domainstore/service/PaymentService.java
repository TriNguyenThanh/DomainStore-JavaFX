
package com.utc2.domainstore.service;

import com.utc2.domainstore.dao.PaymentHistoryDAO;
import com.utc2.domainstore.entity.database.PaymentHistoryModel;
import com.utc2.domainstore.entity.database.PaymentStatusEnum;
import com.utc2.domainstore.entity.database.PaymentTypeEnum;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;

public class PaymentService {
    private ArrayList<PaymentHistoryModel> listPaymentHistory = PaymentHistoryDAO.getInstance().selectAll();
    private final PaymentHistoryDAO paymentHistoryDAO = new PaymentHistoryDAO();

    public JSONObject getUserPaymentHistory(int userId){
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

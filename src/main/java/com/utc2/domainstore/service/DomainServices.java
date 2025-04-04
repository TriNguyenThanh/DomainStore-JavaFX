package com.utc2.domainstore.service;

import com.utc2.domainstore.dao.DomainDAO;
import com.utc2.domainstore.dao.TopLevelDomainDAO;
import com.utc2.domainstore.entity.database.DomainModel;
import com.utc2.domainstore.entity.database.TopLevelDomainModel;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

public class DomainServices {
    private final DomainDAO domainDAO = new DomainDAO();
    
    // 1. tìm theo tên
    public JSONObject search(JSONObject jsonInput) {
        JSONObject jsonObject = new JSONObject(jsonInput);
        String domainName = jsonObject.getString("name");

        List<DomainModel> domainList = domainDAO.searchByName(domainName);
        JSONArray domainArray = new JSONArray();

        for (DomainModel domain : domainList) {
            TopLevelDomainModel tld = domain.getTopLevelDomainbyId(domain.getTldId());
            JSONObject domainJson = new JSONObject();
            domainJson.put("name", domain.getDomainName());
            domainJson.put("status", domain.getStatus().toString().toLowerCase());
            domainJson.put("price", (tld != null) ? tld.getPrice() : 0);
            domainArray.put(domainJson);
        }

        JSONObject response = new JSONObject();
        response.put("domain", domainArray);
        return response;  // Không cần toString()
    }




    // 2. lấy giỏ hàng theo id
    public JSONObject getShoppingCart(JSONObject jsonInput) {
        int userId = jsonInput.getInt("user_id");

        List<DomainModel> cartList = domainDAO.getCartByUserId(userId);
        JSONArray domainArray = new JSONArray();

        for (DomainModel domain : cartList) {
            TopLevelDomainModel tld = domain.getTopLevelDomainbyId(domain.getTldId());
            JSONObject domainJson = new JSONObject();
            domainJson.put("name", domain.getDomainName() + tld.getTldText());
            domainJson.put("status", domain.getStatus().toString().toLowerCase());
            domainJson.put("price", (tld != null) ? tld.getPrice() : 0);
            domainArray.put(domainJson);
        }

        JSONObject response = new JSONObject();
        response.put("domain", domainArray);
        return response;
    }



    // 3. thêm giỏ hàng
    public JSONObject addToCart(JSONObject jsonInput) {
        int userId = jsonInput.getInt("userID");
        JSONArray domainArray = jsonInput.getJSONArray("domain");

        int successCount = 0;

        for (int i = 0; i < domainArray.length(); i++) {
            JSONObject domainJson = domainArray.getJSONObject(i);

            String domainName = domainJson.getString("name");
            String status = domainJson.getString("status");
            int years = domainJson.getInt("years");
            int inputPrice = domainJson.getInt("price");

            if (!"available".equalsIgnoreCase(status)) {
                continue;
            }

            List<DomainModel> matchingDomains = domainDAO.searchByName(domainName);
            DomainModel selectedDomain = null;

            for (DomainModel domain : matchingDomains) {
                if (domain.getStatus() == DomainModel.DomainStatusEnum.AVAILABLE) {
                    selectedDomain = domain;
                    break;
                }
            }

            if (selectedDomain != null) {
                TopLevelDomainModel tld = selectedDomain.getTopLevelDomainbyId(selectedDomain.getTldId());
                int actualPrice = (tld != null) ? tld.getPrice() : 0;

                if (inputPrice != actualPrice) {
                    continue; // Bỏ qua nếu giá không khớp
                }

                selectedDomain.setYears(years);

                boolean isUpdated = domainDAO.updateDomainOwnership(userId, selectedDomain);
                if (isUpdated) successCount++;
            }
        }

        JSONObject response = new JSONObject();
        if (successCount > 0) {
            response.put("status", "success");
            response.put("message", successCount + " domains have been added to " + userId + " cart");
        } else {
            response.put("status", "failed");
            response.put("message", "Failed to add to " + userId + " cart");
        }

        return response;
    }

}

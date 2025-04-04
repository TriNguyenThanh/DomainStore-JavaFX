package com.utc2.domainstore.service;

import com.utc2.domainstore.repository.DomainRepository;
import com.utc2.domainstore.entity.database.DomainModel;
import com.utc2.domainstore.entity.database.TopLevelDomainModel;
import com.utc2.domainstore.repository.TopLevelDomainRepository;
import com.utc2.domainstore.utils.DomainUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

public class DomainServices implements IDomain{
    private final DomainRepository domainDAO = new DomainRepository();
    
    // 1. tìm theo tên
    @Override
    public JSONObject search(JSONObject jsonInput) {
        String domainName = jsonInput.optString("name", "UNKNOWN");
         // Tách phần mở rộng (TLD) từ tên miền
         String[] parts = domainName.split("\\.");
         if (parts.length < 2) {
             return createErrorResponse("Invalid domain format.");
         }
         String tld = "." + parts[parts.length - 1];
         
         // Kiểm tra TLD có hợp lệ không
         TopLevelDomainModel tldModel = TopLevelDomainRepository.getInstance().getTLDByName(tld);
         if (tldModel == null) {
             return createErrorResponse("TLD is not supported.");
         }

       // Kiểm tra domain đã đăng ký chưa
         String domainStatus = DomainUtils.getDomainInfo(domainName);
         JSONObject response = new JSONObject();

        if ("Nofound".equals(domainStatus)) {
             response.put("status", "available");
             response.put("price", tldModel.getPrice());
         } else {
             response.put("status", "sold");
             response.put("price", 0);
         }
 
         response.put("name", domainName);
        return response; 
    }
    private JSONObject createErrorResponse(String message) {
         JSONObject response = new JSONObject();
         response.put("status", "failed");
         response.put("message", message);
         return response;
     }



    // 2. lấy giỏ hàng theo id
//    @Override
//    public JSONObject getShoppingCart(JSONObject jsonInput) {
//        int userId = jsonInput.getInt("user_id");
//
//        List<DomainModel> cartList = domainDAO.getCartByUserId(userId);
//        JSONArray domainArray = new JSONArray();
//
//        for (DomainModel domain : cartList) {
//            TopLevelDomainModel tld = domain.getTopLevelDomainbyId(domain.getTldId());
//            JSONObject domainJson = new JSONObject();
//            domainJson.put("name", domain.getDomainName() + tld.getTldText());
//            domainJson.put("status", domain.getStatus().toString().toLowerCase());
//            domainJson.put("price", (tld != null) ? tld.getPrice() : 0);
//            domainArray.put(domainJson);
//        }
//
//        JSONObject response = new JSONObject();
//        response.put("domain", domainArray);
//        return response;
//    }



    // 3. thêm giỏ hàng
//    @Override
//    public JSONObject addToCart(JSONObject jsonInput) {
//        int userId = jsonInput.getInt("userID");
//        JSONArray domainArray = jsonInput.getJSONArray("domain");
//
//        int successCount = 0;
//
//        for (int i = 0; i < domainArray.length(); i++) {
//            JSONObject domainJson = domainArray.getJSONObject(i);
//
//            String domainName = domainJson.getString("name");
//            String status = domainJson.getString("status");
//            int years = domainJson.getInt("years");
//            int inputPrice = domainJson.getInt("price");
//
//            if (!"available".equalsIgnoreCase(status)) {
//                continue;
//            }
//
//            List<DomainModel> matchingDomains = domainDAO.searchByName(domainName);
//            DomainModel selectedDomain = null;
//
//            for (DomainModel domain : matchingDomains) {
//                if (domain.getStatus() == DomainModel.DomainStatusEnum.AVAILABLE) {
//                    selectedDomain = domain;
//                    break;
//                }
//            }
//
//            if (selectedDomain != null) {
//                TopLevelDomainModel tld = selectedDomain.getTopLevelDomainbyId(selectedDomain.getTldId());
//                int actualPrice = (tld != null) ? tld.getPrice() : 0;
//
//                if (inputPrice != actualPrice) {
//                    continue; // Bỏ qua nếu giá không khớp
//                }
//
//                selectedDomain.setYears(years);
//
//                boolean isUpdated = domainDAO.updateDomainOwnership(userId, selectedDomain);
//                if (isUpdated) successCount++;
//            }
//        }
//
//        JSONObject response = new JSONObject();
//        if (successCount > 0) {
//            response.put("status", "success");
//            response.put("message", successCount + " domains have been added to " + userId + " cart");
//        } else {
//            response.put("status", "failed");
//            response.put("message", "Failed to add to " + userId + " cart");
//        }
//
//        return response;
//    }
    //4. Gợi ý tên miền
    @Override
    public JSONObject suggestion(JSONObject jsonInput) {
        String domainName = jsonInput.getString("name");

        List<DomainModel> domainList = domainDAO.searchByName(domainName);
        JSONArray domainArray = new JSONArray();

        for (DomainModel domain : domainList) {
            TopLevelDomainModel tld = domain.getTopLevelDomainbyId(domain.getTldId());
            JSONObject domainJson = new JSONObject();

            // Lấy phần mở rộng tên miền (TLD)
            String fullDomainName = domain.getDomainName();
            if (tld != null && tld.getTldText() != null) {
                fullDomainName += tld.getTldText();
            }

            domainJson.put("name", fullDomainName);
            domainJson.put("status", domain.getStatus().toString().toLowerCase());
            domainJson.put("price", (tld != null) ? tld.getPrice() : 0);
            domainArray.put(domainJson);
        }

        JSONObject response = new JSONObject();
        response.put("domain", domainArray);
        return response; 
    }
}

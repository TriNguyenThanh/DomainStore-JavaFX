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
         String domainName = jsonInput.optString("name", "").trim();
         // Nếu không nhập tên miền
         if (domainName.isEmpty()){
            JSONArray suggestions = new JSONArray();
            List<DomainModel> domainList = DomainRepository.getInstance().getSuggestedDomains(5);
            
            for (DomainModel domain : domainList){
                JSONObject items = new JSONObject();
                items.put("name", domain.getDomainName());
                items.put("status", domain.getStatus().toString().toLowerCase());
                TopLevelDomainModel tld = TopLevelDomainRepository.getInstance().selectById(new TopLevelDomainModel(domain.getTldId()));
                items.put("price", tld != null ? tld.getPrice() : 0);
                suggestions.put(items);
            }
            
            JSONObject response = new JSONObject();
            response.put("domain", suggestions);
            return response;
         }

         // Nếu có nhập tên miền
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

    //2. Gợi ý tên miền
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

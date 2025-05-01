package com.utc2.domainstore.service;

import com.utc2.domainstore.entity.database.CustomerModel;
import com.utc2.domainstore.entity.database.DomainStatusEnum;
import com.utc2.domainstore.repository.CustomerRepository;
import com.utc2.domainstore.repository.DomainRepository;
import com.utc2.domainstore.entity.database.DomainModel;
import com.utc2.domainstore.entity.database.TopLevelDomainModel;
import com.utc2.domainstore.repository.TopLevelDomainRepository;
import com.utc2.domainstore.utils.DomainUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

public class DomainServices implements IDomain{
    private DomainRepository domainDAO;
    
    // 1. tìm theo tên
    @Override
    public JSONObject search(JSONObject jsonInput) {
        String domainName = jsonInput.optString("name", "").trim();

        if (domainName.isBlank()) {
            JSONArray suggestions = new JSONArray();
            List<DomainModel> domainList = DomainRepository.getInstance().getSuggestedDomains(5);

            for (DomainModel domain : domainList) {
                JSONObject items = new JSONObject();
                TopLevelDomainModel tld = TopLevelDomainRepository.getInstance()
                    .selectById(new TopLevelDomainModel(domain.getTldId()));

                String fullDomainName = domain.getDomainName();
                if (tld != null && tld.getTldText() != null) {
                    fullDomainName += tld.getTldText(); 
                }

                items.put("name", fullDomainName);
                items.put("status", domain.getStatus().toString().toLowerCase());
                items.put("price", tld != null ? tld.getPrice() : 0);
                suggestions.put(items);
            }

            JSONObject response = new JSONObject();
            response.put("domain", suggestions);
            return response;
        }

        // Nếu có nhập tên miền
        String[] parts = domainName.split("\\.");
        String tld;
        if (parts.length < 2) {
            tld = ".com";
            domainName += tld;
        } else {
            tld = "." + parts[parts.length - 1];
        }
        TopLevelDomainModel tldModel = TopLevelDomainRepository.getInstance().getTLDByName(tld);

        if (tldModel == null) {
            return createErrorResponse("TLD is not supported.");
        }

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

    @Override
    public JSONObject searchSoldDomainByCusId(JSONObject jsonInput) {
        int cus_id = jsonInput.getInt("user_id");
        
        List<DomainModel> domainList = DomainRepository.getInstance().getSoldDomains(cus_id);
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
            domainJson.put("year", domain.getYears());
            domainJson.put("price", (tld != null) ? tld.getPrice() : 0);
            domainJson.put("active_date", domain.getActiveDate());
            domainArray.put(domainJson);
        }

        JSONObject response = new JSONObject();
        response.put("domain", domainArray);
        return response; 
    }

    @Override
    public JSONObject getAllDomains() {
        List<DomainModel> domainList = DomainRepository.getInstance().selectAll();
        JSONArray domainArray = new JSONArray();
        
        for (DomainModel domain : domainList){
            TopLevelDomainModel tld = domain.getTopLevelDomainbyId(domain.getTldId());
            JSONObject domainJson = new JSONObject();
            
            String fullNameDomain = domain.getDomainName();
            if (tld != null && tld.getTldText() != null){
                fullNameDomain += tld.getTldText();
            }
            
            domainJson.put("id", domain.getId());
            domainJson.put("name", fullNameDomain);
            domainJson.put("status", domain.getStatus().toString().toLowerCase());
            domainJson.put("year", domain.getYears());
            domainJson.put("price", (tld != null) ? tld.getPrice() : 0);
            domainJson.put("active_date", (domain.getActiveDate() != null) ? domain.getActiveDate() : 0);
            domainJson.put("owner_id", (domain.getOwnerId() != null) ? domain.getOwnerId() : 0);
            // Thêm phần lấy tên người dùng nếu đã bán
            if ("sold".equalsIgnoreCase(domain.getStatus().toString()) && domain.getOwnerId() != null) {
                CustomerModel owner = CustomerRepository.getInstance().selectById(new CustomerModel(domain.getOwnerId()));
                domainJson.put("user_name", (owner != null) ? owner.getFullName() : JSONObject.NULL);
            } else {
                domainJson.put("user_name", JSONObject.NULL);
            }
            domainArray.put(domainJson);
        }
        JSONObject response = new JSONObject();
        response.put("domain", domainArray);
        return response; 
    }

    @Override
    public JSONObject insertNewDomain(JSONObject jsonInput) {
        JSONObject response = new JSONObject();

        if (!jsonInput.has("name")) {
            response.put("status", "error");
            response.put("message", "Missing 'name' field.");
            return response;
        }

        String fullName = jsonInput.getString("name").toLowerCase().trim();

        int dotIndex = fullName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fullName.length() - 1) {
            response.put("status", "error");
            response.put("message", "Invalid domain format.");
            return response;
        }

        String domainName = fullName.substring(0, dotIndex);
        String tldText = "." + fullName.substring(dotIndex + 1);

        // Lấy TLD từ database
        TopLevelDomainModel tld = TopLevelDomainRepository.getInstance().getTLDByName(tldText);
        if (tld == null) {
            response.put("status", "error");
            response.put("message", "Top-level domain not supported.");
            return response;
        }

        // Kiểm tra domain đã tồn tại chưa
        DomainModel existing = DomainRepository.getInstance().getDomainByNameAndTld(domainName, tld.getId());
        if (existing != null) {
            response.put("status", "error");
            response.put("message", "Domain name is already taken.");
            return response;
        }

        // Thêm domain mới vào database
        DomainModel newDomain = new DomainModel();
        newDomain.setDomainName(domainName);
        newDomain.setTldId(tld.getId());
        newDomain.setStatus(DomainStatusEnum.available);

        int insertSuccess = DomainRepository.getInstance().insert(newDomain);
        if (insertSuccess > 0) {
            response.put("status", "success");
            response.put("message", "Domain name is available for registration.");
        } else {
            response.put("status", "error");
            response.put("message", "Failed to insert new domain.");
        }
        return response;
    }

    @Override
    public JSONObject deleteAvailableDomain(JSONObject jsonObject) {
        JSONObject response = new JSONObject();

        if (!jsonObject.has("name")) {
            response.put("status", "failed");
            response.put("message", "Missing 'name' field.");
            return response;
        }

        String fullName = jsonObject.getString("name").toLowerCase().trim();
        int dotIndex = fullName.lastIndexOf('.');

        if (dotIndex == -1 || dotIndex == fullName.length() - 1) {
            response.put("status", "failed");
            response.put("message", "Invalid domain format.");
            return response;
        }

        String domainName = fullName.substring(0, dotIndex);
        String tldText = "." + fullName.substring(dotIndex + 1);

        TopLevelDomainModel tld = TopLevelDomainRepository.getInstance().getTLDByName(tldText);
        if (tld == null) {
            response.put("status", "failed");
            response.put("message", "Top-level domain not supported.");
            return response;
        }

        DomainModel domain = DomainRepository.getInstance().getDomainByNameAndTld(domainName, tld.getId());
        if (domain == null) {
            response.put("status", "failed");
            response.put("message", "Domain not found.");
            return response;
        }

        if (!"available".equalsIgnoreCase(domain.getStatus().toString())) {
            response.put("status", "failed");
            response.put("message", "Only available domains can be deleted.");
            return response;
        }

        int deleted = DomainRepository.getInstance().delete(domain);
        if (deleted > 0) {
            response.put("status", "success");
            response.put("message", "Domain deleted successfully.");
        } else {
            response.put("status", "failed");
            response.put("message", "Failed to delete domain.");
        }

        return response;
    }
}

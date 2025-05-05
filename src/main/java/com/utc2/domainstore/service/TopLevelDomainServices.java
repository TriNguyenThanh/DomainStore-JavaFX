package com.utc2.domainstore.service;

import com.utc2.domainstore.entity.database.DomainModel;
import com.utc2.domainstore.entity.database.DomainStatusEnum;
import com.utc2.domainstore.entity.database.TopLevelDomainModel;
import com.utc2.domainstore.repository.DomainRepository;
import com.utc2.domainstore.repository.TopLevelDomainRepository;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class TopLevelDomainServices implements ITopLevelDomain{
    private TopLevelDomainRepository tldDAO;
    
    @Override
    public JSONObject updateTLD(JSONObject jsonInput) {
        JSONObject result = new JSONObject();
        try{
            int id = jsonInput.getInt("id");
            String TLD_text = jsonInput.getString("TLD_text");
            int price = jsonInput.getInt("price");

            if (!TLD_text.matches("^\\.[a-z]{2,}$")) {
                result.put("status", "fail");
                result.put("message", "Invalid TLD format. Must be like .com, .vn, etc.");
                return result;
            }

            int success = TopLevelDomainRepository.getInstance().update(new TopLevelDomainModel(id,TLD_text,price));
            DomainRepository.getInstance().updateByStatusAndTldId(price, id);
            if (success > 0){
                result.put("status", "success");
                result.put("message", "Updated successfully");
            } else{
                result.put("status", "fail");
                result.put("message", "Updated failed (Id may not exist)");
            }
        } catch (Exception e){
            result.put("status", "failed");
            result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public JSONObject getAllTLD() {
        List<TopLevelDomainModel> tldModelList = TopLevelDomainRepository.getInstance().selectAll();
        JSONArray tldArray = new JSONArray();
        
        for (TopLevelDomainModel tldModel : tldModelList){
            JSONObject tldJson = new JSONObject();
            
            tldJson.put("id", tldModel.getId());
            tldJson.put("TLD_text", tldModel.getTldText());
            tldJson.put("price", tldModel.getPrice());
            tldArray.put(tldJson);
        }
        JSONObject response = new JSONObject();
        response.put("tld", tldArray);
        return response; 
    }
    
}

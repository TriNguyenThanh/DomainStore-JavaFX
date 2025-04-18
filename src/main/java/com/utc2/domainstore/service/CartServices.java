package com.utc2.domainstore.service;

import com.utc2.domainstore.entity.database.DomainModel;
import com.utc2.domainstore.entity.database.DomainStatusEnum;
import com.utc2.domainstore.entity.database.TopLevelDomainModel;
import com.utc2.domainstore.repository.CartRepository;
import com.utc2.domainstore.repository.DomainRepository;
import com.utc2.domainstore.repository.TopLevelDomainRepository;
import java.sql.Timestamp;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class CartServices implements ICart {

    private final CartRepository cartRepository;

    public CartServices(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public JSONObject getShoppingCart(JSONObject jsonInput) {
        JSONObject response = new JSONObject();
        JSONArray domainArray = new JSONArray();
        // Lấy cus_id từ request
        int cus_id = jsonInput.getInt("cus_id");

        // Truy vấn danh sách tên miền trong giỏ hàng của user
        List<DomainModel> cartItems = cartRepository.getCartByUserId(cus_id);

        for (DomainModel domain : cartItems) {
            TopLevelDomainModel tld = domain.getTopLevelDomainbyId(domain.getTldId());
            JSONObject domainJson = new JSONObject();

            // Lấy phần mở rộng tên miền (TLD)
            String fullDomainName = domain.getDomainName();
            if (tld != null && tld.getTldText() != null) {
                fullDomainName += tld.getTldText();
            }

            domainJson.put("name", fullDomainName);
            domainJson.put("status", domain.getStatus().toString());
            domainJson.put("price", DomainRepository.getInstance().getTLDPriceByDomainId(domain.getTldId()));

            domainArray.put(domainJson);
        }

        response.put("domain", domainArray);
        return response;
    }

    @Override
    public JSONObject addToCart(JSONObject jsonInput) {
        int cus_id = jsonInput.getInt("cus_id");
        JSONArray domainArray = jsonInput.getJSONArray("domain");

        int successCount = 0;
        boolean domainNotFound = false;
        JSONObject response = new JSONObject();
        for (int i = 0; i < domainArray.length(); i++) {
            JSONObject domainJson = domainArray.getJSONObject(i);
            String domainName = domainJson.getString("name");
            String status = domainJson.getString("status");
            int years = domainJson.getInt("years");
            int inputPrice = domainJson.getInt("price");
            
            // Tách phần mở rộng (TLD) từ tên miền
            String[] parts = domainName.split("\\.");
            if (parts.length < 2) {
                response.put("status", "failed");
                response.put("message", "domain_id not found");
            }
            String tld = "." + parts[parts.length - 1];
            TopLevelDomainModel domainId = TopLevelDomainRepository.getInstance().getTLDByName(tld);
            if (!"available".equalsIgnoreCase(status)) {
                continue;
            }
            String name = domainName.contains(".") ? domainName.split("\\.")[0] : domainName;
            //nếu domain chưa có trong db thì sẽ insert vào đấy
            if(!DomainRepository.getInstance().isDomainExists(domainName, domainId.getId())){
                DomainModel newDomain = new DomainModel(name, domainId.getId(), DomainStatusEnum.available);
                DomainRepository.getInstance().insert(newDomain);
                boolean isAdded = cartRepository.addToCart(cus_id, newDomain.getId(), years);
            if (isAdded) successCount++;
            } else{
                DomainModel domainModel = DomainRepository.getInstance().getDomainByNameAndTld(name, domainId.getId());
                boolean isAdded = cartRepository.addToCart(cus_id, domainModel.getId(), years);
                if (isAdded) successCount++;
            }
        }

        if (domainNotFound) {
            response.put("status", "failed");
            response.put("message", "domain_id not found");
        } else if (successCount > 0) {
            response.put("status", "success");
            response.put("message", successCount + " domains added to cart");
        } else {
            response.put("status", "failed");
            response.put("message", "No domains added");
        }

        return response;
    }
}

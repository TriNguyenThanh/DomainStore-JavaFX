package com.utc2.domainstore.service;

import com.utc2.domainstore.entity.database.DomainModel;
import com.utc2.domainstore.repository.CartRepository;
import com.utc2.domainstore.repository.DomainRepository;
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
        DomainRepository domainRepo = new DomainRepository();
        // Lấy cus_id từ request
        int cus_id = jsonInput.getInt("cus_id");

        // Truy vấn danh sách tên miền trong giỏ hàng của user
        List<DomainModel> cartItems = cartRepository.getCartByUserId(cus_id);
        
        for (DomainModel domain : cartItems) {
            JSONObject domainJson = new JSONObject();
            domainJson.put("name", domain.getDomainName());
            domainJson.put("status", domain.getStatus().toString());
            domainJson.put("price", domainRepo.getTLDPriceByDomainId(cus_id));

            domainArray.put(domainJson);
        }

        response.put("domain", domainArray);
        return response;
    }

    @Override
    public JSONObject addToCart(JSONObject jsonInput) {
        int userId = jsonInput.getInt("user_id");
        JSONArray domainArray = jsonInput.getJSONArray("domain_id");

        int successCount = 0;

        for (int i = 0; i < domainArray.length(); i++) {
            JSONObject domainJson = domainArray.getJSONObject(i);
            int domainId = domainJson.getInt("id");
            int years = domainJson.getInt("years");

            // Kiểm tra xem domain có sẵn hay không
            if (!cartRepository.isDomainAvailable(domainId)) {
                continue;
            }

            // Thêm vào giỏ hàng
            boolean isAdded = cartRepository.addToCart(userId, domainId, years);
            if (isAdded) successCount++;
        }

        JSONObject response = new JSONObject();
        response.put("status", successCount > 0 ? "success" : "failed");
        response.put("message", successCount > 0 ? successCount + " domains added to cart" : "No domains added");

        return response;
    }
}

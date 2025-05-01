package com.utc2.domainstore.service;

import com.utc2.domainstore.entity.database.DomainModel;
import com.utc2.domainstore.entity.database.DomainStatusEnum;
import com.utc2.domainstore.entity.database.TopLevelDomainModel;
import com.utc2.domainstore.repository.CartRepository;
import com.utc2.domainstore.repository.DomainRepository;
import com.utc2.domainstore.repository.TopLevelDomainRepository;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class CartServices implements ICart {

    private CartRepository cartRepository;

    public CartServices() {
        this.cartRepository = new CartRepository();
    }

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
            domainJson.put("year", domain.getYears());

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
                domainNotFound = true;
                continue;
            }

            String tld = "." + parts[parts.length - 1];
            TopLevelDomainModel domainId = TopLevelDomainRepository.getInstance().getTLDByName(tld);
            if (domainId == null || !"available".equalsIgnoreCase(status)) {
                continue;
            }

            String name = parts[0];

            if (!DomainRepository.getInstance().isDomainExists(domainName, domainId.getId())) {
                // Nếu domain chưa tồn tại trong DB insert mới
                DomainModel newDomain = new DomainModel(name, domainId.getId(), DomainStatusEnum.available, years);
                DomainRepository.getInstance().insert(newDomain);

                // Lấy lại domain sau khi insert
                DomainModel domainModel = DomainRepository.getInstance().getDomainByNameAndTld(name, domainId.getId());

                // Kiểm tra đã có trong cart chưa
                if (!cartRepository.isDomainInCart2(cus_id, domainModel.getId())) {
                    boolean isAdded = cartRepository.updateCart(cus_id, domainModel.getId(), years);
                    if (isAdded) successCount++;
                }
            } else {
                // Domain đã tồn tại trong DB
                DomainModel domainModel = DomainRepository.getInstance().getDomainByNameAndTld(name, domainId.getId());
                DomainModel updateDomain = new DomainModel(
                        domainModel.getId(),
                        domainModel.getDomainName(),
                        domainModel.getTldId(),
                        domainModel.getStatus(),
                        domainModel.getActiveDate(),
                        years);
                DomainRepository.getInstance().update(updateDomain);
                // Kiểm tra đã có trong cart chưa
                if (!cartRepository.isDomainInCart(cus_id, domainModel.getId(), years)) {
                    cartRepository.updateCart(cus_id, domainModel.getId(), years);
                    successCount++;
                }
            }
        }

        if (domainNotFound) {
            response.put("status", "failed");
            response.put("message", "domain_id not found");
        } else if (successCount > 0) {
            response.put("status", "success");
            response.put("message", successCount + " domain(s) added to cart");
        } else {
            response.put("status", "failed");
            response.put("message", "No domains added (maybe already in cart)");
        }

        return response;
    }

    @Override
    public JSONObject removeFromCart(JSONObject jsonInput) {
        int cus_id = jsonInput.getInt("cus_id");
        JSONArray domainArray = jsonInput.getJSONArray("domain");

        int successCount = 0;
        JSONObject response = new JSONObject();

        for (int i = 0; i < domainArray.length(); i++) {
            JSONObject domainJson = domainArray.getJSONObject(i);
            String domainName = domainJson.getString("name");

            String[] parts = domainName.split("\\.");
            if (parts.length < 2) {
                continue;
            }

            String tld = "." + parts[parts.length - 1];
            TopLevelDomainModel tldModel = TopLevelDomainRepository.getInstance().getTLDByName(tld);

            if (tldModel == null) {
                continue;
            }

            String name = parts[0];

            DomainModel domainModel = DomainRepository.getInstance().getDomainByNameAndTld(name, tldModel.getId());
            if (domainModel != null) {
                if (cartRepository.isDomainInCart2(cus_id, domainModel.getId())) {
                    boolean isRemoved = cartRepository.removeFromCart(cus_id, domainModel.getId());
                    if (isRemoved) successCount++;
                }
            }
        }

        if (successCount > 0) {
            response.put("status", "success");
            response.put("message", successCount + " domain(s) removed from cart");
        } else {
            response.put("status", "failed");
            response.put("message", "No domains removed (maybe not found in cart)");
        }

        return response;
    }
}

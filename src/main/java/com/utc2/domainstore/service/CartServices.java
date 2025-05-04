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

            // Lấy tên miền đầy đủ
            String fullDomainName = domain.getDomainName();
            if (tld != null && tld.getTldText() != null) {
                fullDomainName += tld.getTldText();
            }

            domainJson.put("name", fullDomainName);
            domainJson.put("status", domain.getStatus().toString().toLowerCase());
            domainJson.put("price", (tld != null) ? tld.getPrice() : 0);
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

        // Lấy danh sách TLD và sắp xếp theo độ dài giảm dần
        List<TopLevelDomainModel> tldList = TopLevelDomainRepository.getInstance().selectAll();
        tldList.sort((a, b) -> b.getTldText().length() - a.getTldText().length()); // Ưu tiên TLD dài nhất

        for (int i = 0; i < domainArray.length(); i++) {
            JSONObject domainJson = domainArray.getJSONObject(i);
            String fullDomainName = domainJson.getString("name").toLowerCase().trim();
            String status = domainJson.getString("status");
            int years = domainJson.getInt("years");

            if (!"available".equalsIgnoreCase(status)) continue;

            // Dò TLD từ đuôi tên miền, ưu tiên TLD dài nhất
            TopLevelDomainModel matchedTLD = null;
            String namePart = null;
            for (TopLevelDomainModel tld : tldList) {
                if (fullDomainName.endsWith(tld.getTldText())) {
                    matchedTLD = tld;
                    namePart = fullDomainName.substring(0, fullDomainName.length() - tld.getTldText().length());
                    break;
                }
            }

            if (matchedTLD == null || namePart == null || namePart.isBlank()) {
                domainNotFound = true;
                continue;
            }

            DomainModel domainModel = DomainRepository.getInstance().getDomainByNameAndTld(namePart, matchedTLD.getId());
            if (domainModel == null) {
                // Insert mới nếu chưa tồn tại
                domainModel = new DomainModel(namePart, matchedTLD.getId(), DomainStatusEnum.available, years);
                DomainRepository.getInstance().insert(domainModel);
                domainModel = DomainRepository.getInstance().getDomainByNameAndTld(namePart, matchedTLD.getId());
            } else {
                // Cập nhật số năm nếu đã có
                domainModel.setYears(years);
                DomainRepository.getInstance().update(domainModel);
            }

            // Thêm vào giỏ nếu chưa có
            if (!cartRepository.isDomainInCart(cus_id, domainModel.getId(), years)) {
                if (cartRepository.updateCart(cus_id, domainModel.getId(), years)) {
                    successCount++;
                }
            }
        }

        if (domainNotFound) {
            response.put("status", "failed");
            response.put("message", "One or more domains have invalid format or unsupported TLD.");
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

        List<TopLevelDomainModel> allTlds = TopLevelDomainRepository.getInstance().selectAll();

        for (int i = 0; i < domainArray.length(); i++) {
            JSONObject domainJson = domainArray.getJSONObject(i);
            String fullDomainName = domainJson.getString("name").toLowerCase().trim();

            TopLevelDomainModel matchedTld = null;
            String domainNamePart = null;

            for (TopLevelDomainModel tld : allTlds) {
                if (fullDomainName.endsWith(tld.getTldText())) {
                    matchedTld = tld;
                    domainNamePart = fullDomainName.substring(0, fullDomainName.length() - tld.getTldText().length());
                    break;
                }
            }

            if (matchedTld == null || domainNamePart == null || domainNamePart.isBlank()) {
                continue;
            }

            DomainModel domainModel = DomainRepository.getInstance().getDomainByNameAndTld(domainNamePart, matchedTld.getId());
            if (domainModel != null && cartRepository.isDomainInCart2(cus_id, domainModel.getId())) {
                boolean isRemoved = cartRepository.removeFromCart(cus_id, domainModel.getId());
                if (isRemoved) successCount++;
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

    @Override
    public JSONObject updateCart(JSONObject jsonInput) {
        int cus_id = jsonInput.getInt("cus_id");
        JSONArray domainArray = jsonInput.getJSONArray("domain");

        int updateCount = 0;
        boolean domainNotFound = false;
        JSONObject response = new JSONObject();

        List<TopLevelDomainModel> allTlds = TopLevelDomainRepository.getInstance().selectAll();

        for (int i = 0; i < domainArray.length(); i++) {
            JSONObject domainJson = domainArray.getJSONObject(i);
            String fullDomainName = domainJson.getString("name").toLowerCase().trim();
            String status = domainJson.getString("status");
            int years = domainJson.getInt("years");

            TopLevelDomainModel matchedTld = null;
            String domainNamePart = null;

            for (TopLevelDomainModel tld : allTlds) {
                if (fullDomainName.endsWith(tld.getTldText())) {
                    matchedTld = tld;
                    domainNamePart = fullDomainName.substring(0, fullDomainName.length() - tld.getTldText().length());
                    break;
                }
            }

            if (matchedTld == null || domainNamePart == null || domainNamePart.isBlank()) {
                domainNotFound = true;
                continue;
            }

            if (!"available".equalsIgnoreCase(status)) {
                continue;
            }

            DomainModel domainModel = DomainRepository.getInstance().getDomainByNameAndTld(domainNamePart, matchedTld.getId());

            if (domainModel == null) {
                // Domain chưa tồn tại → insert
                DomainModel newDomain = new DomainModel(domainNamePart, matchedTld.getId(), DomainStatusEnum.available, years);
                DomainRepository.getInstance().insert(newDomain);
                domainModel = DomainRepository.getInstance().getDomainByNameAndTld(domainNamePart, matchedTld.getId());
            } else {
                // Domain đã tồn tại → cập nhật số năm
                DomainModel updatedDomain = new DomainModel(
                        domainModel.getId(),
                        domainModel.getDomainName(),
                        domainModel.getTldId(),
                        domainModel.getStatus(),
                        domainModel.getActiveDate(),
                        years);
                DomainRepository.getInstance().update(updatedDomain);
            }

            // Cập nhật hoặc thêm vào giỏ hàng
            boolean updated = cartRepository.updateCart(cus_id, domainModel.getId(), years);
            if (updated) updateCount++;
        }

        if (domainNotFound) {
            response.put("status", "failed");
            response.put("message", "Invalid domain name(s)");
        } else if (updateCount > 0) {
            response.put("status", "success");
            response.put("message", updateCount + " domain(s) updated in cart");
        } else {
            response.put("status", "failed");
            response.put("message", "No domains updated");
        }

        return response;
    }
}

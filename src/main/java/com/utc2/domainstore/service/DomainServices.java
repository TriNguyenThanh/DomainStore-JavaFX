package com.utc2.domainstore.service;

import com.utc2.domainstore.dao.DomainDAO;
import com.utc2.domainstore.model.DomainModel;
import com.utc2.domainstore.model.TopLevelDomainModel;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.Scanner;

public class DomainServices {
    private final DomainDAO domainDAO = new DomainDAO();
    
    // 1. tìm theo tên
    public String search(String jsonInput) {
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
        return response.toString();
    }

    // 2. lấy giỏ hàng theo id
    public String getShoppingCart(String jsonInput) {
        JSONObject jsonObject = new JSONObject(jsonInput);
        int userId = jsonObject.getInt("user_id");
        
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
        return response.toString();
    }



    // 3. thêm giỏ hàng
    public String addToCart(String jsonInput) {
        JSONObject jsonObject = new JSONObject(jsonInput);
        int userId = jsonObject.getInt("userID");
        JSONArray domainArray = jsonObject.getJSONArray("domain");

        int successCount = 0;

        for (int i = 0; i < domainArray.length(); i++) {
            JSONObject domainJson = domainArray.getJSONObject(i);

            //Đọc đúng tên miền từ JSON
            String domainName = domainJson.getString("domain_name");
            String tldText = domainJson.getString("tld");

            //Tìm kiếm domain dựa trên domain_name và tld
            List<DomainModel> matchingDomains = domainDAO.searchByName(domainName);
            DomainModel selectedDomain = null;

            for (DomainModel domain : matchingDomains) {
                TopLevelDomainModel tldModel = domain.getTopLevelDomainbyId(domain.getTldId());
                if (tldModel != null && tldModel.getTldText().equalsIgnoreCase(tldText)) {
                    selectedDomain = domain;
                    break;
                }
            }

            //Kiểm tra domain có sẵn để thêm vào giỏ hàng
            if (selectedDomain != null && selectedDomain.getStatus() == DomainModel.DomainStatusEnum.AVAILABLE) {

                // Cập nhật số năm đăng ký từ JSON
                int years = domainJson.getInt("years");
                selectedDomain.setYears(years); 

                // Gọi hàm cập nhật owner_id và years (mới sửa)
                boolean isUpdated = domainDAO.updateDomainOwnership(userId, selectedDomain);
                if (isUpdated) successCount++;
            }
        }

        //Tạo phản hồi JSON
        JSONObject response = new JSONObject();
        if (successCount > 0) {
            response.put("status", "success");
            response.put("message", successCount + " domains have been added to " + userId + " cart");
        } else {
            response.put("status", "failed");
            response.put("message", "Failed to add to " + userId + " cart");
        }

        return response.toString();
    }




    // Main
    public static void main(String[] args) {
        DomainServices service = new DomainServices();
        Scanner scanner = new Scanner(System.in);

        // Test search
//        System.out.println("Nhập để tìm kiếm:");
//        String searchJson = scanner.nextLine();
//        System.out.println(service.search(searchJson));

        // Test get shopping cart
//        System.out.println("Enter JSON for shopping cart:");
//        String cartJson = scanner.nextLine();
//        System.out.println(service.getShoppingCart(cartJson));

        // Test add to cart
//        String testJson = "{"
//        + "\"userID\": 1,"
//        + "\"domain\": ["
//        + "{"
//        + "\"domain_name\": \"example\","
//        + "\"tld\": \".com\","
//        + "\"price\": 299000,"
//        + "\"years\": 1"
//        + "},"
//        + "{"
//        + "\"domain_name\": \"example\","
//        + "\"tld\": \".site\","
//        + "\"price\": 39000,"
//        + "\"years\": 1"
//        + "}"
//        + "]"
//        + "}";
//        String response = service.addToCart(testJson);
//        System.out.println("Response: " + response);

        scanner.close();
    }
}

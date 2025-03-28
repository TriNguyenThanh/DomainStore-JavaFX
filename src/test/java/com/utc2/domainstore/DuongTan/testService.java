package com.utc2.domainstore.DuongTan;

import org.json.JSONObject;
import org.json.JSONArray;
import com.utc2.domainstore.service.DomainServices;
import com.utc2.domainstore.service.LoginServices;
import com.utc2.domainstore.service.RegisterServices;

public class testService {
    public static void main(String[] args) {
        //TEST ĐĂNG KÝ NGƯỜI DÙNG
        RegisterServices registerServices = new RegisterServices();
        JSONObject registerInput = new JSONObject();
        registerInput.put("username", "Nguyen Van A");
        registerInput.put("phone", "0987654321");
        registerInput.put("email", "nguyenvana@example.com");
        registerInput.put("personal_id", "123456789");
        registerInput.put("password", "mypassword");

        JSONObject registerResponse = registerServices.addToDB(registerInput);
        System.out.println("Register Response: " + registerResponse.toString(2));


        //TEST ĐĂNG NHẬP NGƯỜI DÙNG
        LoginServices loginServices = new LoginServices();
        JSONObject loginInput = new JSONObject();
        loginInput.put("username", "0987654321");
        loginInput.put("password", "mypassword");

        JSONObject loginResponse = loginServices.authentication(loginInput);
        System.out.println("Login Response: " + loginResponse.toString(2));

        //TEST TÌM KIẾM DOMAIN

        DomainServices domainServices = new DomainServices();
        JSONObject searchInput = new JSONObject();
        searchInput.put("name", "example");

        JSONObject searchResponse = domainServices.search(searchInput);
        System.out.println("Search Response: " + searchResponse.toString(2));

        //TEST THÊM DOMAIN VÀO GIỎ HÀNG
        JSONObject cartInput = new JSONObject();
        cartInput.put("userID", 1);
        
        JSONArray domainArray = new JSONArray();
        JSONObject domain1 = new JSONObject();
        domain1.put("domain_name", "example");
        domain1.put("tld", ".com");
        domain1.put("years", 1);
        
        domainArray.put(domain1);
        cartInput.put("domain", domainArray);

        JSONObject cartResponse = domainServices.addToCart(cartInput);
        System.out.println("Add to Cart Response: " + cartResponse.toString(2));

        //TEST LẤY GIỎ HÀNG
        JSONObject cartRequest = new JSONObject();
        cartRequest.put("user_id", 1);
        
        JSONObject cartResult = domainServices.getShoppingCart(cartRequest);
        System.out.println("Get Shopping Cart Response: " + cartResult.toString(2));
    }
}

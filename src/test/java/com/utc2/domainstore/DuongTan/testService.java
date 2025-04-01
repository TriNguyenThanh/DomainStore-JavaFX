package com.utc2.domainstore.DuongTan;

import com.utc2.domainstore.service.AccountServices;
import org.json.JSONObject;
import org.json.JSONArray;
import com.utc2.domainstore.service.DomainServices;
import com.utc2.domainstore.service.LoginServices;
import com.utc2.domainstore.service.RegisterServices;

public class testService {
    public static void main(String[] args) {
        //TEST ĐĂNG KÝ NGƯỜI DÙNG
//        RegisterServices registerServices = new RegisterServices();
//        JSONObject registerInput = new JSONObject();
//        registerInput.put("username", "Nguyen Van A");
//        registerInput.put("phone", "0987654321");
//        registerInput.put("email", "nguyenvana@example.com");
//        registerInput.put("personal_id", "123456789");
//        registerInput.put("password", "mypassword");
//
//        JSONObject registerResponse = registerServices.addToDB(registerInput);
//        System.out.println("Register Response: " + registerResponse.toString(2));
//
//
//        //TEST ĐĂNG NHẬP NGƯỜI DÙNG
//        LoginServices loginServices = new LoginServices();
//        JSONObject loginInput = new JSONObject();
//        loginInput.put("username", "0987654321");
//        loginInput.put("password", "pass123456@");
//
//        JSONObject loginResponse = loginServices.authentication(loginInput);
//        System.out.println("Login Response: " + loginResponse.toString(2));
//
//        //TEST TÌM KIẾM DOMAIN
//
//        DomainServices domainServices = new DomainServices();
//        JSONObject searchInput = new JSONObject();
//        searchInput.put("name", "example");
//
//        JSONObject searchResponse = domainServices.search(searchInput);
//        System.out.println("Search Response: " + searchResponse.toString(2));

        //TEST THÊM DOMAIN VÀO GIỎ HÀNG
//        DomainServices domainServices = new DomainServices();
//        JSONObject cartInput = new JSONObject();
//        cartInput.put("userID", 1);
//        
//        JSONArray domainArray = new JSONArray();
//        JSONObject domain1 = new JSONObject();
//        domain1.put("name", "example");
//        domain1.put("status", "available");
//        domain1.put("price", 299000);
//        domain1.put("years", 1);
//        
//        domainArray.put(domain1);
//        cartInput.put("domain", domainArray);
//
//        JSONObject cartResponse = domainServices.addToCart(cartInput);
//        System.out.println("Add to Cart Response: " + cartResponse.toString(2));

        //TEST LẤY GIỎ HÀNG
//    	DomainServices domainServices = new DomainServices();
//        JSONObject cartRequest = new JSONObject();
//        cartRequest.put("user_id", 1);
//        
//        JSONObject cartResult = domainServices.getShoppingCart(cartRequest);
//        System.out.println("Get Shopping Cart Response: " + cartResult.toString(2));
        
        
        // TEST CẬP NHẬT THÔNG TIN NGƯỜI DÙNG (KHÔNG BAO GỒM MẬT KHẨU)
//        AccountServices accountServices = new AccountServices();
//        JSONObject updateUserInput = new JSONObject();
//        updateUserInput.put("user_id", 1);
//        updateUserInput.put("username", "Au Duong Tai");
//        updateUserInput.put("phone", "0123456789");
//        updateUserInput.put("email", "auduongtai27@gmail.com");
//        updateUserInput.put("personal_id", "027205011960");
//        
//        JSONObject updateUserResponse = accountServices.updateUser(updateUserInput);
//        System.out.println("Update User Response: " + updateUserResponse.toString(2));
        
        // TEST CẬP NHẬT MẬT KHẨU NGƯỜI DÙNG
//        AccountServices accountServices = new AccountServices();
//        JSONObject updatePasswordInput = new JSONObject();
//        updatePasswordInput.put("user_id", 1);
//        updatePasswordInput.put("password", "newpassword123");
//        
//        JSONObject updatePasswordResponse = accountServices.updateUserPassword(updatePasswordInput);
//        System.out.println("Update Password Response: " + updatePasswordResponse.toString(2));

        // TEST LẤY THÔNG TIN NGƯỜI DÙNG THEO ID
//        AccountServices accountServices = new AccountServices();
//        JSONObject getUserInput = new JSONObject();
//        getUserInput.put("user_id", 1);
//
//        JSONObject getUserResponse = accountServices.getUserInformation(getUserInput);
//        System.out.println("Get User Information Response: " + getUserResponse.toString(2));

        // TEST LẤY DANH SÁCH TẤT CẢ NGƯỜI DÙNG
//        AccountServices accountServices = new AccountServices();
//        JSONObject getAllUsersResponse = accountServices.getAllUserAccount();
//        System.out.println("Get All Users Response: " + getAllUsersResponse.toString(2));
        }
}

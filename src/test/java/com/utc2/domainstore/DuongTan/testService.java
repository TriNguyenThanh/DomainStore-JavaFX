package com.utc2.domainstore.DuongTan;

import com.utc2.domainstore.entity.database.DomainStatusEnum;
import com.utc2.domainstore.repository.CartRepository;
import com.utc2.domainstore.service.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class testService {
    public static void main(String[] args) {

        // ====== TEST 1: Đăng ký người dùng ======
//        RegisterServices registerServices = new RegisterServices();
//        JSONObject registerInput = new JSONObject();
//        registerInput.put("username", "Nguyen Van A");
//        registerInput.put("phone", "0987654321");
//        registerInput.put("email", "nguyenvanabc@example.com");
//        registerInput.put("personal_id", "027205011960");
//        registerInput.put("password", "mypassword");
//        registerInput.put("role", "user"); 
//        JSONObject registerResponse = registerServices.addToDB(registerInput);
//        System.out.println("Register Response: " + registerResponse.toString(2));

        // ====== TEST 2: Đăng nhập người dùng ======
//        LoginServices loginServices = new LoginServices();
//        JSONObject loginInput = new JSONObject();
//        loginInput.put("username", "0987654321");
//        loginInput.put("password", "pass123456@");
//        JSONObject loginResponse = loginServices.authentication(loginInput);
//        System.out.println("Login Response: " + loginResponse.toString(2));

        // ====== TEST 3: Tìm kiếm domain theo tên ======
//        DomainServices domainServices = new DomainServices();
//        JSONObject searchInput = new JSONObject();
//        searchInput.put("name", "coolbrand.com");
//        JSONObject searchResponse = domainServices.search(searchInput);
//        System.out.println("Search Response: " + searchResponse.toString(2));

        // ====== TEST 4: Tìm kiếm domain khi không nhập gì ======
//        DomainServices domainServices = new DomainServices();
//        JSONObject searchInput = new JSONObject();
//        searchInput.put("name", "");
//        JSONObject searchResponse = domainServices.search(searchInput);
//        System.out.println("Search Response (empty): " + searchResponse.toString(2));

        // ====== TEST 5: Gợi ý domain dựa theo tên ======
//        DomainServices domainServices = new DomainServices();
//        JSONObject suggestionInput = new JSONObject();
//        suggestionInput.put("name", "example.com");
//        JSONObject suggestionResponse = domainServices.suggestion(suggestionInput);
//        System.out.println("Suggestion Response: " + suggestionResponse.toString(2));

        // ====== TEST 6: Tìm kiếm domain đã bán theo user_id ======
//        DomainServices domainServices = new DomainServices();
//        JSONObject soldDomainInput = new JSONObject();
//        soldDomainInput.put("user_id", 1);
//        JSONObject soldDomainResponse = domainServices.searchSoldDomainByCusId(soldDomainInput);
//        System.out.println("Sold Domain Response: " + soldDomainResponse.toString(2));

        // ====== TEST 7: Cập nhật thông tin người dùng (không đổi mật khẩu) ======
//        AccountServices accountServices = new AccountServices();
//        JSONObject updateUserInput = new JSONObject();
//        updateUserInput.put("user_id", 1);
//        updateUserInput.put("username", "Au Duong Tai");
//        updateUserInput.put("phone", "0123456789");
//        updateUserInput.put("email", "auduongtai27@gmail.com");
//        updateUserInput.put("personal_id", "027205011960");
//        JSONObject updateUserResponse = accountServices.updateUser(updateUserInput);
//        System.out.println("Update User Response: " + updateUserResponse.toString(2));

        // ====== TEST 8: Cập nhật mật khẩu người dùng ======
//        AccountServices accountServices = new AccountServices();
//        JSONObject updatePasswordInput = new JSONObject();
//        updatePasswordInput.put("user_id", 1);
//        updatePasswordInput.put("password", "newpassword123");
//        JSONObject updatePasswordResponse = accountServices.updateUserPassword(updatePasswordInput);
//        System.out.println("Update Password Response: " + updatePasswordResponse.toString(2));

        // ====== TEST 9: Lấy thông tin người dùng theo ID ======
//        AccountServices accountServices = new AccountServices();
//        JSONObject getUserInput = new JSONObject();
//        getUserInput.put("user_id", 1);
//        JSONObject getUserResponse = accountServices.getUserInformation(getUserInput);
//        System.out.println("Get User Information Response: " + getUserResponse.toString(2));

        // ====== TEST 10: Lấy danh sách tất cả người dùng ======
//        AccountServices accountServices = new AccountServices();
//        JSONObject getAllUsersResponse = accountServices.getAllUserAccount();
//        System.out.println("Get All Users Response: " + getAllUsersResponse.toString(2));

        // ====== TEST 11: Lấy giỏ hàng người dùng ======
//        CartServices cartServices = new CartServices();
//        JSONObject cartInput = new JSONObject();
//        cartInput.put("cus_id", 1);
//        JSONObject cartResponse = cartServices.getShoppingCart(cartInput);
//        System.out.println("Shopping Cart Response: " + cartResponse.toString(2));

        // ====== TEST 12: Thêm domain vào giỏ hàng ======
//        CartServices cartServices = new CartServices();
//        JSONObject addToCartInput = new JSONObject();
//        addToCartInput.put("cus_id", 1);
//        JSONArray domainArray = new JSONArray();
//        JSONObject domain1 = new JSONObject();
//        domain1.put("name", "tanvip.com");
//        domain1.put("status", "available");
//        domain1.put("price", 299000);
//        domain1.put("years", 2);
//        domainArray.put(domain1);
//        addToCartInput.put("domain", domainArray);
//        JSONObject addToCartResponse = cartServices.addToCart(addToCartInput);
//        System.out.println("Add to Cart Response: " + addToCartResponse.toString(2));

        // ====== TEST 13: Xóa domain khỏi giỏ hàng ======
//        CartServices cartServices = new CartServices();
//        JSONObject removeFromCartInput = new JSONObject();
//        removeFromCartInput.put("cus_id", 1);
//        JSONArray removeDomainArray = new JSONArray();
//        JSONObject domainToRemove = new JSONObject();
//        domainToRemove.put("name", "diamonielts.com");
//        removeDomainArray.put(domainToRemove);
//        removeFromCartInput.put("domain", removeDomainArray);
//        JSONObject removeFromCartResponse = cartServices.removeFromCart(removeFromCartInput);
//        System.out.println("Remove from Cart Response: " + removeFromCartResponse.toString(2));

        // ====== TEST 14: Xóa tài khoản người dùng ======
//        AccountServices accountServices = new AccountServices();
//        JSONObject deleteUserInput = new JSONObject();
//        deleteUserInput.put("user_id", 2);
//        JSONObject deleteUserResponse = accountServices.lockedAccount(deleteUserInput);
//        System.out.println("Delete User Response: " + deleteUserResponse.toString(2));
    }
}

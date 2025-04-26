package com.utc2.domainstore.DuongTan;
import com.utc2.domainstore.service.AccountServices;
import com.utc2.domainstore.repository.CartRepository;
import com.utc2.domainstore.service.CartServices;
import com.utc2.domainstore.entity.database.DomainStatusEnum;
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

//        DomainServices domainServices = new DomainServices();
//        JSONObject searchInput = new JSONObject();
//        searchInput.put("name", "coolbrand.com");
//        System.out.println("Test Input: " + searchInput.toString(2));
//        JSONObject searchResponse = domainServices.search(searchInput);
//        System.out.println("Search Response: " + searchResponse.toString(2));

          // test kiểm tra tìm kiếm tên miền nếu như người dùng không nhập gì hết
//        DomainServices domainServices = new DomainServices();
//        JSONObject searchInput = new JSONObject();
//        searchInput.put("name", "");
//        JSONObject searchResponse = domainServices.search(searchInput);
//        System.out.println("Search Response: " + searchResponse.toString(2));
        
            // test gợi ý tên miền
//        DomainServices domainServices = new DomainServices();
//        JSONObject searchInput = new JSONObject();
//        searchInput.put("name", "example.com");
// 
//        JSONObject searchResponse = domainServices.suggestion(searchInput);
//        System.out.println("Search Response: " + searchResponse.toString(2));
        
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
        

////        //test cart
//         Khởi tạo CartRepository (giả định)
//        CartRepository cartRepository = new CartRepository();

        // Tạo đối tượng CartServices
//        CartServices cartServices = new CartServices();
////
        // TEST LẤY GIỎ HÀNG NGƯỜI DÙNG
//        JSONObject cartInput = new JSONObject();
//        cartInput.put("cus_id", 4);
//
//        JSONObject cartResponse = cartServices.getShoppingCart(cartInput);
//        System.out.println("Shopping Cart Response: " + cartResponse.toString(2));

        // TEST THÊM DOMAIN VÀO GIỎ HÀNG
        
//        JSONObject addToCartInput = new JSONObject();
//        addToCartInput.put("cus_id", 1);
//
//        JSONArray domainArray = new JSONArray();
//        JSONObject domain1 = new JSONObject();
//        domain1.put("name", "yourexample.com");
//        domain1.put("status", "available");
//        domain1.put("price", 299000);
//        domain1.put("years", 3);
//        domainArray.put(domain1); 
//        addToCartInput.put("domain", domainArray);
//
//        JSONObject addToCartResponse = cartServices.addToCart(addToCartInput);
//        System.out.println("Add to Cart Response: " + addToCartResponse.toString(2));

        //TEST xóa khỏi giỏ hàng

//        CartServices cartServices = new CartServices();
//        JSONObject input = new JSONObject();
//        input.put("cus_id", 1); 
//        JSONArray domainArray = new JSONArray();
//
//        JSONObject domain1 = new JSONObject();
//        domain1.put("name", "diamonielts.com"); 
//        domainArray.put(domain1);
//
//        input.put("domain", domainArray);
//
//        JSONObject result = cartServices.removeFromCart(input);
//
//        System.out.println("Kết quả xóa domain khỏi giỏ:");
//        System.out.println(result.toString(4));
    }
}

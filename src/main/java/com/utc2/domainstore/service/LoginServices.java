package com.utc2.domainstore.service;

import com.utc2.domainstore.dao.CustomerDAO;
import com.utc2.domainstore.entity.database.CustomerModel;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONObject;

public class LoginServices {
    public String authentication(String jsonInput) {
        
        // b1: tìm trong database xem có user nào có username này hay không. Username là số điện thoại
        // nếu có thì qua b2, không thì return ""
        
        // b2: lấy mã salt trong database rồi băm password ra hash code rồi so sánh với hash code của user
        // nếu đúng thì return ID
     
        JSONObject jsonObject = new JSONObject(jsonInput);
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        
        CustomerDAO customerDAO = new CustomerDAO();
        CustomerModel customer = customerDAO.selectByPhone(username);
        
        if (customer == null) {
            return new JSONObject().put("error", "User not found").toString();
        }
        
        //kiểm tra lại mật khẩu băm
        Argon2 argon2 = Argon2Factory.create();
        boolean isVerified = argon2.verify(customer.getPasswordHash(),password);
        
        Map<String, Object> responseMap = new LinkedHashMap<>();
        if (isVerified == true) {
            responseMap.put("user_id", customer.getId());
            responseMap.put("role", customer.getRole());
        } else {
            responseMap.put("user_id", 0);
            responseMap.put("role", "user");
        }

        return new JSONObject(responseMap).toString(); // Đảm bảo giữ nguyên thứ tự
    }
    //{"username": "0987654321", "password": "pass123456@"}
    public static void main(String[] args) {
        LoginServices a = new LoginServices();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Nhập JSON đăng nhập:");
        String jsonInput = scanner.nextLine();

        String response = a.authentication(jsonInput);
        System.out.println("Phản hồi từ server:");
        System.out.println(response);

        scanner.close();
    }
}
package com.utc2.domainstore.service;

import com.utc2.domainstore.dao.CustomerDAO;
import com.utc2.domainstore.entity.database.CustomerModel;
import com.utc2.domainstore.utils.PasswordUtils;

import java.util.Scanner;
import org.json.JSONObject;
public class RegisterServices {
    
    public String addToDB(String jsonInput) {
        // them khach hang moi vao database
        // them thanh cong thi tra ve true
        // them that bai thi tra ve false

        // Chuyển chuỗi JSON đầu vào thành đối tượng JSON
        JSONObject jsonObject = new JSONObject(jsonInput);
        String name = jsonObject.getString("username");
        String phone = jsonObject.getString("phone");
        String email = jsonObject.getString("email");
        String personalId = jsonObject.getString("personal_id");
        String password = jsonObject.getString("password");
        
        // Tạo DAO để tương tác với database
        CustomerDAO customerDAO = new CustomerDAO();
        
        // Kiểm tra xem số điện thoại đã tồn tại chưa
        if (customerDAO.selectByPhone(phone) != null) {
            return createResponse("failed", "Phone number already exists.");
        }
        
        // Băm mật khẩu
        String hashedPassword = PasswordUtils.hashedPassword(password);
        
        // Tạo đối tượng khách hàng mới
        CustomerModel newCustomer = new CustomerModel(name, email, phone, personalId, hashedPassword, CustomerModel.Role.user);

        // Thêm vào database
        int result = customerDAO.insert(newCustomer);

        if (result > 0) {
            return createResponse("success", "");
        } else {
            return createResponse("failed", "");
        }
    }
    
    // Hàm tạo JSON phản hồi
    private String createResponse(String status, String message) {
        return "{ \"status\": \"" + status + "\", \"message\": \"" + message + "\" }";
    }
    public static void main(String[] args) {
        RegisterServices a = new RegisterServices();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Nhập JSON đăng nhập:");
        String jsonInput = scanner.nextLine();

        String response = a.addToDB(jsonInput);
        System.out.println("Phản hồi từ server:");
        System.out.println(response);

        scanner.close();
    }
    /*
    {"username": "Au Duong Tan","phone": "0375283079","email": "auduongtan321@gmail.com","personal_id": "027205011959","password": "password"}
    {"username": "Au Duong Tai","phone": "0961683079","email": "auduongtai27@gmail.com","personal_id": "027205011958","password": "password2"}
    */
}

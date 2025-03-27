package com.utc2.domainstore.service;

import com.utc2.domainstore.dao.CustomerDAO;
import com.utc2.domainstore.entity.database.CustomerModel;
import com.utc2.domainstore.utils.PasswordUtils;
import java.sql.Timestamp;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class AccountServices {
    private final CustomerDAO customerDAO = new CustomerDAO();
    
    //1. Lấy thông tin user theo user_id
    public String getUserInformation(String jsonInput) {
        JSONObject jsonObject = new JSONObject(jsonInput);
        int user_id = jsonObject.getInt("user_id");
        
        CustomerModel customer = new CustomerModel(user_id, "", "", "", "", "",CustomerModel.Role.user, null);
        CustomerModel find = customerDAO.selectById(customer);
        if (find == null) {
            return createResponse("failed", "User not found");
        }

        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("username", find.getFullName());
        responseMap.put("phone", find.getPhone());
        responseMap.put("email", find.getEmail());
        responseMap.put("personal_id", find.getCccd());
        responseMap.put("password", find.getPasswordHash());

        JSONObject response = new JSONObject(responseMap);
        return response.toString();
    }
    
    //2. update user
    public String updateUser(String jsonInput) {
        JSONObject jsonObject = new JSONObject(jsonInput);
        int userId = jsonObject.getInt("user_id");
        String name = jsonObject.getString("username");
        String phone = jsonObject.getString("phone");
        String email = jsonObject.getString("email");
        String personalId = jsonObject.getString("personal_id");
        String password = jsonObject.getString("password");

        // Kiểm tra người dùng có tồn tại không
        CustomerModel customer = new CustomerModel(userId, "", "", "", "", "",CustomerModel.Role.user, null);
        CustomerModel existingUser = customerDAO.selectById(customer);
        if (existingUser == null) {
            return createResponse("failed", "User not found");
        }

        // Hash lại mật khẩu
        String hashedPassword = PasswordUtils.hashedPassword(password);

        // Cập nhật thông tin
        CustomerModel updatedUser = new CustomerModel(existingUser.getId(),name, email, phone, personalId, hashedPassword, existingUser.getRole(),new Timestamp(System.currentTimeMillis()));
        int result = customerDAO.update(updatedUser);

        if (result > 0) {
            return createResponse("success", "");
        } else {
            return createResponse("failed", "");
        }
    }
    
    //3. Lấy danh sách tất cả người dùng
    public String getAllUserAccount() {
        List<CustomerModel> userList = customerDAO.selectAll();
        JSONArray userArray = new JSONArray();

        for (CustomerModel user : userList) {
            JSONObject userJson = new JSONObject();
            userJson.put("user_id", user.getId());
            userJson.put("username", user.getFullName());
            userJson.put("phone", user.getPhone());
            userJson.put("email", user.getEmail());
            userJson.put("personal_id", user.getCccd());

            userArray.put(userJson);
        }

        JSONObject response = new JSONObject();
        response.put("user", userArray);
        return response.toString();
    }
    
    //tạo JSON phản hồi
    private String createResponse(String status, String message) {
        JSONObject response = new JSONObject();
        response.put("status", status);
        response.put("message", message);
        return response.toString();
    }
    
    //test
    public static void main(String[] args) {
        AccountServices service = new AccountServices();
        Scanner scanner = new Scanner(System.in);

        // Test getUserInformation
        //{"user_id":1}
//        System.out.println("\nNhập JSON tìm kiếm user:");
//        String getJson = scanner.nextLine();
//        scanner.nextLine(); 
//        System.out.println(service.getUserInformation(getJson));

        // Test updateUser
        //{"user_id":12,"username":"Au Duong Phong","phone":"0698710129","email":"auduongphong@gmail.com","personal_id":"027205011947","password":"newpassword123"} 
//        System.out.println("\nNhập JSON cập nhật user:");
//        String updateJson = scanner.nextLine();
//        System.out.println(service.updateUser(updateJson));

        // Test getAllUserAccount
//        System.out.println("\nDanh sách tất cả người dùng:");
//        System.out.println(service.getAllUserAccount());

        scanner.close();
    }
}

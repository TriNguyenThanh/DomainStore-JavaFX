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
    
    // 1. Lấy thông tin user theo user_id
    public JSONObject getUserInformation(JSONObject jsonInput) {
        int user_id = jsonInput.getInt("user_id");

        CustomerModel customer = new CustomerModel(user_id, "", "", "", "", "", CustomerModel.Role.user, null);
        CustomerModel find = customerDAO.selectById(customer);
        if (find == null) {
            return createResponse("failed", "User not found");
        }

        JSONObject response = new JSONObject();
        response.put("username", find.getFullName());
        response.put("phone", find.getPhone());
        response.put("email", find.getEmail());
        response.put("personal_id", find.getCccd());
        response.put("password", find.getPasswordHash());

        return response;
    }
    
    // 2. Cập nhật user
    public JSONObject updateUser(JSONObject jsonInput) {
        int userId = jsonInput.getInt("user_id");
        String name = jsonInput.getString("username");
        String phone = jsonInput.getString("phone");
        String email = jsonInput.getString("email");
        String personalId = jsonInput.getString("personal_id");
        String password = jsonInput.getString("password");

        // Kiểm tra người dùng có tồn tại không
        CustomerModel customer = new CustomerModel(userId, "", "", "", "", "", CustomerModel.Role.user, null);
        CustomerModel existingUser = customerDAO.selectById(customer);
        if (existingUser == null) {
            return createResponse("failed", "User not found");
        }

        // Hash lại mật khẩu
        String hashedPassword = PasswordUtils.hashedPassword(password);

        // Cập nhật thông tin
        CustomerModel updatedUser = new CustomerModel(
            existingUser.getId(), name, email, phone, personalId, hashedPassword, existingUser.getRole(),
            new Timestamp(System.currentTimeMillis())
        );
        int result = customerDAO.update(updatedUser);

        return result > 0 ? createResponse("success", "User updated successfully") 
                          : createResponse("failed", "Update failed");
    }
    
    // 3. Lấy danh sách tất cả người dùng
    public JSONObject getAllUserAccount() {
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
        response.put("users", userArray);
        return response;
    }
    
    // Tạo JSON phản hồi chung
    private JSONObject createResponse(String status, String message) {
        JSONObject response = new JSONObject();
        response.put("status", status);
        response.put("message", message);
        return response;
    }
}

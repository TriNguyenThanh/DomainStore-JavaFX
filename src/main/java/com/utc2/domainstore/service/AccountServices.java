package com.utc2.domainstore.service;

import com.utc2.domainstore.entity.database.CustomerModel;
import com.utc2.domainstore.entity.database.RoleEnum;
import com.utc2.domainstore.repository.CustomerRepository;
import com.utc2.domainstore.utils.PasswordUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.List;

public class AccountServices implements IAccount {
    private final CustomerRepository customerDAO = new CustomerRepository();

    // 1. Lấy thông tin user theo user_id
    @Override
    public JSONObject getUserInformation(JSONObject jsonInput) {
        int user_id = jsonInput.getInt("user_id");

        CustomerModel customer = new CustomerModel(user_id, "", "", "", "", "", RoleEnum.user, false, null);
        CustomerModel find = customerDAO.selectById(customer);
        if (find == null || find.getIsDeleted() == true) {
            return createResponse("failed", "User not found or is locked (deleted)");
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
    //update người dùng không update mật khẩu
    @Override
    public JSONObject updateUser(JSONObject jsonInput) {
        int userId = jsonInput.getInt("user_id");

        // Lấy dữ liệu user cũ từ database
        CustomerModel existingUser = customerDAO.selectById(new CustomerModel(userId, "", "", "", "", "", RoleEnum.user, false, null));
        if (existingUser == null) {
            return createResponse("failed", "User not found");
        }

        // Giữ nguyên dữ liệu cũ nếu không có dữ liệu mới
        String name = jsonInput.has("username") ? jsonInput.getString("username") : existingUser.getFullName();
        String phone = jsonInput.has("phone") ? jsonInput.getString("phone") : existingUser.getPhone();
        String email = jsonInput.has("email") ? jsonInput.getString("email") : existingUser.getEmail();
        String personalId = jsonInput.has("personal_id") ? jsonInput.getString("personal_id") : existingUser.getCccd();

        // Xử lý role (nếu có)
        RoleEnum role = existingUser.getRole();
        if (jsonInput.has("role")) {
            try {
                role = RoleEnum.valueOf(jsonInput.getString("role")); // ví dụ: "user" hoặc "admin"
            } catch (IllegalArgumentException e) {
                return createResponse("failed", "Invalid role value");
            }
        }

        // Cập nhật thông tin
        CustomerModel updatedUser = new CustomerModel(
                existingUser.getId(), name, email, phone, personalId, role, new Timestamp(System.currentTimeMillis())
        );

        int result = customerDAO.update(updatedUser);

        return result > 0 ? createResponse("success", "User updated successfully")
                : createResponse("failed", "Update failed");
    }

    //update mật khẩu người dùng 
    @Override
    public JSONObject updateUserPassword(JSONObject jsonInput) {
        int userId = jsonInput.getInt("user_id");
        String password = jsonInput.getString("password");

        //kiểm tra người dùng có tồn tại hay không
        CustomerModel existingCustomer = customerDAO.selectById(new CustomerModel(userId, "", "", "", "", "", RoleEnum.user, false, null));
        if (existingCustomer == null) {
            return createResponse("failed", "User not found");
        }

        // hash mật khẩu mới
        String hashedPassword = PasswordUtils.hashedPassword(password);
        existingCustomer.setPasswordHash(hashedPassword);
        int result = customerDAO.update(existingCustomer);

        return result > 0 ? createResponse("success", "Ur password updated successfully")
                : createResponse("failed", "Ur password update failed");
    }

    // 3. Lấy danh sách tất cả người dùng
    @Override
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
            userJson.put("role", user.getRole());
            userJson.put("is_deleted", user.getIsDeleted());
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

    //xóa tài khoản
    @Override
    public JSONObject lockedAccount(JSONObject jsonInput) {
        int user_id = jsonInput.getInt("user_id");

        // Lấy user hiện tại từ DB
        CustomerModel existingUser = CustomerRepository.getInstance().selectById(new CustomerModel(user_id));
        if (existingUser == null) {
            return createResponse("failed", "User not found");
        }

        // Đặt cờ is_deleted = true
        existingUser.setDeleted(true);

        // Cập nhật lại người dùng
        int resultUpdate = CustomerRepository.getInstance().update(existingUser);

        return resultUpdate > 0 ? createResponse("success", "User locked successfully")
                : createResponse("failed", "Lock failed");
    }
}

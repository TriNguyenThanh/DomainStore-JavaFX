package com.utc2.domainstore.service;

import com.utc2.domainstore.entity.database.CustomerModel;
import com.utc2.domainstore.entity.database.RoleEnum;
import com.utc2.domainstore.repository.CustomerRepository;
import com.utc2.domainstore.utils.EmailUtil;
import com.utc2.domainstore.utils.PasswordUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

public class AccountServices implements IAccount {
    private final CustomerRepository customerDAO = new CustomerRepository();

    // 1. Lấy thông tin user theo user_id
    @Override
    public JSONObject getUserInformation(JSONObject jsonInput) {
        int user_id = jsonInput.getInt("user_id");

        CustomerModel customer = new CustomerModel(user_id, "", "", "", "", RoleEnum.USER, false, null);
        CustomerModel find = customerDAO.selectById(customer);
        if (find == null || find.getIsDeleted() == true) {
            return createResponse("failed", "User not found or is locked (deleted)");
        }

        JSONObject response = new JSONObject();
        response.put("username", find.getFullName());
        response.put("phone", find.getPhone());
        response.put("email", find.getEmail());
        response.put("password", find.getPasswordHash());

        return response;
    }

    // 2. Cập nhật user
    //update người dùng không update mật khẩu
    @Override
    public JSONObject updateUser(JSONObject jsonInput) {
        int userId = jsonInput.getInt("user_id");

        // Lấy dữ liệu user cũ từ database
        CustomerModel existingUser = customerDAO.selectById(new CustomerModel(userId, "", "", "", "", RoleEnum.USER, false, null));
        if (existingUser == null) {
            return createResponse("failed", "User not found");
        }

        // Giữ nguyên dữ liệu cũ nếu không có dữ liệu mới
        String name = jsonInput.has("username") ? jsonInput.getString("username") : existingUser.getFullName();
        String phone = jsonInput.has("phone") ? jsonInput.getString("phone") : existingUser.getPhone();
        String email = jsonInput.has("email") ? jsonInput.getString("email") : existingUser.getEmail();

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
                existingUser.getId(), name, email, phone, role, new Timestamp(System.currentTimeMillis())
        );

        int result = 0;
        try {
            result = customerDAO.update(updatedUser);
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                return createResponse("failed", e.getMessage());
            } else {
                return createResponse("failed", "Update failed");
            }
        }

        return createResponse("success", "User updated successfully");
    }

    //update mật khẩu người dùng 
    @Override
    public JSONObject updateUserPassword(JSONObject jsonInput) {
        int userId = jsonInput.getInt("user_id");
        String password = jsonInput.getString("password");

        //kiểm tra người dùng có tồn tại hay không
        CustomerModel existingCustomer = customerDAO.selectById(new CustomerModel(userId, "", "", "", "", RoleEnum.USER, false, null));
        if (existingCustomer == null) {
            return createResponse("failed", "User not found");
        }

        // hash mật khẩu mới
        String hashedPassword = PasswordUtils.hashedPassword(password);
        existingCustomer.setPasswordHash(hashedPassword);
        int result = 0;
        try {
            result = customerDAO.update(existingCustomer);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

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
        try {
            CustomerRepository.getInstance().update(existingUser);
        } catch (SQLException e) {
            return createResponse("failed", "Lock failed");
        }

        return createResponse("success", "User locked successfully");
    }

    @Override
    public JSONObject sendOtpToUser(JSONObject t) {
        String userPhone = t.getString("phone");
        String userEmail = t.getString("email");

        JSONObject response = new JSONObject();

        // kiểm tra email và phone có tồn tại hay không
        boolean exists = CustomerRepository.getInstance().existsByPhoneAndEmail(userPhone, userEmail);
        if (!exists) {
            response.put("status", "failed");
            response.put("message", "Invalid email or phone number.");
            return response;
        }

        //tạo otp
        String otp = generateOtp();
        String subject = "Mã OTP đặt lại mật khẩu";
        String content = "Mã OTP của bạn là: " + otp + "\nMã này có hiệu lực trong 5 phút.";

        //gọi emailUtil
        EmailUtil.sendEmail(userEmail,subject,content);

        //update Otp trong db
        int result = CustomerRepository.getInstance().updateOtp(userEmail, otp, userPhone);

        // Trả về kết quả JSON
        if (result > 0) {
            response.put("status", "success");
            response.put("message", "OTP sent to email.");
        } else {
            response.put("status", "failed");
            response.put("message", "Invalid email or phone number.");
        }

        return response;
    }

    @Override
    public JSONObject checkingOtp(JSONObject t) {
        String otp = t.getString("otp");
        String userEmail = t.getString("email");

        JSONObject response = new JSONObject();

        CustomerModel user = CustomerRepository.getInstance().selectByEmail(userEmail);

        if (user != null && otp.equals(user.getOtp())) {
            Timestamp otpCreatedAt = user.getOtpCreatedAt();

            if (otpCreatedAt != null) {
                long currentTime = System.currentTimeMillis();
                long otpTime = otpCreatedAt.getTime();

                // Kiểm tra hiệu lực OTP trong 5 phút
                if (currentTime - otpTime <= 5 * 60 * 1000) {
                    response.put("status", "success");
                    response.put("message", "Valid OTP.");
                } else {
                    response.put("status", "fail");
                    response.put("message", "OTP has expired.");
                }
            } else {
                response.put("status", "fail");
                response.put("message", "OTP generation time not found.");
            }
        } else {
            response.put("status", "fail");
            response.put("message", "OTP is incorrect or email does not exist.");
        }

        return response;
    }

    @Override
    public JSONObject updatingNewPassWord(JSONObject t) {
        String userEmail = t.getString("email");
        String newPassword = t.getString("password");
        String hashedPassword = PasswordUtils.hashedPassword(newPassword);

        int result = CustomerRepository.getInstance().updatePasswordByEmail(userEmail, hashedPassword);

        // Trả về kết quả JSON
        JSONObject response = new JSONObject();
        if (result > 0) {
            response.put("status", "success");
            response.put("message", "Change password successfully.");
        } else {
            response.put("status", "fail");
            response.put("message", "Changing password failed.");
        }
        return response;
    }

    public String generateOtp(){
        Random rand = new Random();
        int otp = 100000 + rand.nextInt(900000);
        return String.valueOf(otp);
    }
}

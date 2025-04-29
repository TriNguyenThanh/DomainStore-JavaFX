package com.utc2.domainstore.service;

import com.utc2.domainstore.repository.CustomerRepository;
import com.utc2.domainstore.entity.database.CustomerModel;
import com.utc2.domainstore.entity.database.RoleEnum;
import com.utc2.domainstore.utils.PasswordUtils;
import org.json.JSONObject;
public class RegisterServices implements IRegister{
    
    @Override
    public JSONObject addToDB(JSONObject jsonInput) {
        String name = jsonInput.getString("username");
        String phone = jsonInput.getString("phone");
        String email = jsonInput.getString("email");
        String personalId = jsonInput.getString("personal_id");
        String password = jsonInput.getString("password");
        String role = jsonInput.getString("role");

        CustomerRepository customerDAO = new CustomerRepository();

        // Kiểm tra số điện thoại
        CustomerModel existingPhone = customerDAO.selectByPhone(phone);
        if (existingPhone != null && !existingPhone.getIsDeleted()) {
            return createResponse("failed", "Phone number already exists.");
        }

        // Kiểm tra email
        CustomerModel existingEmail = customerDAO.selectByEmail(email);
        if (existingEmail != null && !existingEmail.getIsDeleted()) {
            return createResponse("failed", "Email already exists.");
        }

        // Kiểm tra CCCD
        CustomerModel existingCCCD = customerDAO.selectByCccd(personalId);
        if (existingCCCD != null && !existingCCCD.getIsDeleted()) {
            return createResponse("failed", "CCCD already exists.");
        }

        String hashedPassword = PasswordUtils.hashedPassword(password);

        // Parse role
        RoleEnum userRole;
        try {
            userRole = RoleEnum.valueOf(role.toLowerCase());
        } catch (IllegalArgumentException e) {
            userRole = RoleEnum.user;
        }

        CustomerModel newCustomer = new CustomerModel(name, email, phone, personalId, hashedPassword, userRole);

        int result = customerDAO.insert(newCustomer);
        if (result > 0) {
            return createResponse("success", "User registered successfully.");
        } else {
            return createResponse("failed", "Failed to register user.");
        }
    }



    private JSONObject createResponse(String status, String message) {
        JSONObject response = new JSONObject();
        response.put("status", status);
        response.put("message", message);
        return response;
    }
}

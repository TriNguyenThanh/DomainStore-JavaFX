package com.utc2.domainstore.service;

import com.utc2.domainstore.dao.CustomerDAO;
import com.utc2.domainstore.entity.database.CustomerModel;
import com.utc2.domainstore.utils.PasswordUtils;
import org.json.JSONObject;
public class RegisterServices {
    
    public JSONObject addToDB(JSONObject jsonInput) {
        String name = jsonInput.getString("username");
        String phone = jsonInput.getString("phone");
        String email = jsonInput.getString("email");
        String personalId = jsonInput.getString("personal_id");
        String password = jsonInput.getString("password");

        CustomerDAO customerDAO = new CustomerDAO();

        JSONObject response = new JSONObject();
        if (customerDAO.selectByPhone(phone) != null) {
            response.put("status", "failed");
            response.put("message", "Phone number already exists.");
            return response;
        }

        String hashedPassword = PasswordUtils.hashedPassword(password);
        CustomerModel newCustomer = new CustomerModel(name, email, phone, personalId, hashedPassword, CustomerModel.Role.user);

        int result = customerDAO.insert(newCustomer);
        if (result > 0) {
            response.put("status", "success");
            response.put("message", "User registered successfully.");
        } else {
            response.put("status", "failed");
            response.put("message", "Failed to register user.");
        }

        return response;
    }
    
    private String createResponse(String status, String message) {
        return "{ \"status\": \"" + status + "\", \"message\": \"" + message + "\" }";
    }
}

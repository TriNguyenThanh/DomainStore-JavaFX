package com.utc2.domainstore.service;

import com.utc2.domainstore.entity.database.CustomerModel;
import com.utc2.domainstore.repository.CustomerRepository;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.json.JSONObject;

public class LoginServices implements ILogin {
    @Override
    public JSONObject authentication(JSONObject jsonInput) {
        String username = jsonInput.getString("username");
        String password = jsonInput.getString("password");

        CustomerRepository customerDAO = new CustomerRepository();
        CustomerModel customer = customerDAO.selectByPhone(username);

        JSONObject response = new JSONObject();
        // Kiểm tra tồn tại và trạng thái bị khóa (xóa mềm)
        if (customer == null) {
            response.put("error", "User not found");
            return response;
        } else if (customer.getIsDeleted()) {
            response.put("error", "User has been locked");
            return response;
        }
        Argon2 argon2 = Argon2Factory.create();
        boolean isVerified = argon2.verify(customer.getPasswordHash(), password);

        if (isVerified) {
            response.put("user_id", customer.getId());
            response.put("role", customer.getRole());
        } else {
            response.put("user_id", 0);
            response.put("role", "user");
        }

        return response;
    }
}
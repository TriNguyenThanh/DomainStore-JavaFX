package com.utc2.domainstore.dao;

import com.utc2.domainstore.utils.JDBC;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.utc2.domainstore.model.CustomerModel;
import com.utc2.domainstore.model.CustomerModel.Role;
import com.utc2.domainstore.utils.PasswordUtils;
import com.utc2.domainstore.dao.DAOInterface;
import java.util.ArrayList;

public class CustomerDAO implements DAOInterface<CustomerModel> {

    public static CustomerDAO getInstance() {
        return new CustomerDAO();
    }

    @Override
    public int insert(CustomerModel customer) {
        // Lưu vào bảng Customer
        try {
            Connection con = JDBC.getConnection();
            String sql = "INSERT INTO users (full_name, email, phone, cccd, password_hash, role) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setString(1, customer.getFullName());
            pst.setString(2, customer.getEmail());
            pst.setString(3, customer.getPhone());
            pst.setString(4, customer.getCccd());
            pst.setString(5, customer.getPasswordHash());
            pst.setString(6, customer.getRole().name());
            
            int result = pst.executeUpdate();
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                customer.setId(rs.getInt(1)); // Lấy ID vừa tạo và gán vào object
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int update(CustomerModel customer) {
        try {
            Connection con = JDBC.getConnection();
            String sql = "UPDATE users SET full_name=?, email=?, phone=?, cccd=?, password_hash=?, role=? WHERE id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, customer.getFullName());
            pst.setString(2, customer.getEmail());
            pst.setString(3, customer.getPhone());
            pst.setString(4, customer.getCccd());
            pst.setString(5, customer.getPasswordHash());
            pst.setString(6, customer.getRole().name());
            pst.setInt(7, customer.getId());
            
            return pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int delete(CustomerModel customer) {
        try {
            Connection con = JDBC.getConnection();
            String sql = "DELETE FROM users WHERE id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, customer.getId());
            return pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public CustomerModel selectById(CustomerModel customer) {
        try {
            Connection con = JDBC.getConnection();
            String sql = "SELECT * FROM users WHERE id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, customer.getId());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new CustomerModel(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("cccd"),
                        rs.getString("password_hash"),
                        Role.valueOf(rs.getString("role")),
                        rs.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<CustomerModel> selectByCondition(String condition) {
        ArrayList<CustomerModel> customer = new ArrayList<>();
        try {
            Connection con = JDBC.getConnection();
            String sql = "SELECT * FROM users WHERE " + condition;
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                customer.add(new CustomerModel(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("cccd"),
                        rs.getString("password_hash"),
                        Role.valueOf(rs.getString("role")),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
         }
        return customer;
    }

    @Override
    public ArrayList<CustomerModel> selectAll() {
        ArrayList<CustomerModel> users = new ArrayList<>();
        try {
            Connection con = JDBC.getConnection();
            String sql = "SELECT * FROM users";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                users.add(new CustomerModel(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("cccd"),
                        rs.getString("password_hash"),
                        Role.valueOf(rs.getString("role")),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return users;
    }
    //lay so dien thoai
    public CustomerModel selectByPhone(String phone) {
        try {
            Connection con = JDBC.getConnection();
            String sql = "SELECT * FROM users WHERE phone=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, phone);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return new CustomerModel(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("cccd"),
                        rs.getString("password_hash"),
                        Role.valueOf(rs.getString("role")),
                        rs.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
}
package com.utc2.domainstore.repository;

import com.utc2.domainstore.config.JDBC;
import com.utc2.domainstore.entity.database.CustomerModel;
import com.utc2.domainstore.entity.database.RoleEnum;

import java.sql.*;
import java.util.ArrayList;

public class CustomerRepository implements IRepository<CustomerModel> {

    public static CustomerRepository getInstance() {
        return new CustomerRepository();
    }

    @Override
    public int insert(CustomerModel customer) {
        String sql = "INSERT INTO users (full_name, email, phone, cccd, password_hash, role) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, customer.getFullName());
            pst.setString(2, customer.getEmail());
            pst.setString(3, customer.getPhone());
            pst.setString(4, customer.getCccd());
            pst.setString(5, customer.getPasswordHash());
            pst.setString(6, customer.getRole().name());

            int result = pst.executeUpdate();

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    customer.setId(rs.getInt(1));
                }
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
    public int update(CustomerModel customer) throws SQLException {
        String sql;
        if (customer.getPasswordHash() == null || customer.getPasswordHash().isEmpty()) {
            sql = "UPDATE users SET full_name=?, email=?, phone=?, cccd=?, role=?, is_deleted=? WHERE id=?";
        } else {
            sql = "UPDATE users SET full_name=?, email=?, phone=?, cccd=?, password_hash=?, role=?, is_deleted=? WHERE id=?";
        }

        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, customer.getFullName());
            pst.setString(2, customer.getEmail());
            pst.setString(3, customer.getPhone());
            pst.setString(4, customer.getCccd());

            if (customer.getPasswordHash() == null || customer.getPasswordHash().isEmpty()) {
                pst.setString(5, customer.getRole().name());
                pst.setBoolean(6, customer.getIsDeleted());
                pst.setInt(7, customer.getId());
            } else {
                pst.setString(5, customer.getPasswordHash());
                pst.setString(6, customer.getRole().name());
                pst.setBoolean(7, customer.getIsDeleted());
                pst.setInt(8, customer.getId());
            }
            try {
                return pst.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException e) {
                throw new SQLException("Duplicate entry: " + e.getMessage());
            }
        } catch (SQLException e) {
            throw new SQLException("Error: " + e.getMessage());
        }
    }
    
    @Override
    public int delete(CustomerModel customer) {
        String sql = "DELETE FROM users WHERE id=?";

        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

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
        String sql = "SELECT * FROM users WHERE id=?";

        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, customer.getId());

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new CustomerModel(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("cccd"),
                            rs.getString("password_hash"),
                            RoleEnum.valueOf(rs.getString("role")),
                            rs.getBoolean("is_deleted"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<CustomerModel> selectByCondition(String condition) {
        ArrayList<CustomerModel> customers = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE " + condition;

        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                customers.add(new CustomerModel(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("cccd"),
                        rs.getString("password_hash"),
                        RoleEnum.valueOf(rs.getString("role")),
                        rs.getBoolean("is_deleted"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return customers;
    }


    @Override
    public ArrayList<CustomerModel> selectAll() {
        ArrayList<CustomerModel> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                users.add(new CustomerModel(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("cccd"),
                        rs.getString("password_hash"),
                        RoleEnum.valueOf(rs.getString("role")),
                        rs.getBoolean("is_deleted"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Lấy thông tin khách hàng theo số điện thoại
    public CustomerModel selectByPhone(String phone) {
        String sql = "SELECT * FROM users WHERE phone = ?";
        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, phone);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new CustomerModel(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("cccd"),
                            rs.getString("password_hash"),
                            RoleEnum.valueOf(rs.getString("role")),
                            rs.getBoolean("is_deleted"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //lấy email
    public CustomerModel selectByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, email);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new CustomerModel(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("cccd"),
                            rs.getString("password_hash"),
                            RoleEnum.valueOf(rs.getString("role")),
                            rs.getBoolean("is_deleted"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //lấy cccd
    public CustomerModel selectByCccd(String cccd) {
        String sql = "SELECT * FROM users WHERE cccd = ?";
        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, cccd);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new CustomerModel(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("cccd"),
                            rs.getString("password_hash"),
                            RoleEnum.valueOf(rs.getString("role")),
                            rs.getBoolean("is_deleted"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
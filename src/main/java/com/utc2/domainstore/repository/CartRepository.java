package com.utc2.domainstore.repository;

import com.utc2.domainstore.entity.database.CartModel;
import com.utc2.domainstore.utils.JDBC;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CartRepository implements IRepository<CartModel>{
    public static CustomerRepository getInstance() {
        return new CustomerRepository();
    }
    
    @Override
    public int insert(CartModel t) {
        String sql = "INSERT INTO carts (cus_id, domain_id, years) VALUES (?, ?, ?)";

        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pst.setInt(1, t.getCus_id());
            pst.setInt(2, t.getDomain_id());
            pst.setInt(3, t.getYears());

            int result = pst.executeUpdate();

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    t.setId(rs.getInt(1)); 
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
    public int update(CartModel t) {
       String sql = "UPDATE carts SET cus_id = ?, domain_id = ?, years = ? WHERE id = ?";
        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, t.getCus_id());
            pst.setInt(2, t.getDomain_id());
            pst.setInt(3, t.getYears());
            pst.setInt(4, t.getId());

            return pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int delete(CartModel t) {
        String sql = "DELETE FROM carts WHERE id=?";
        
        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, t.getId());
            return pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public ArrayList<CartModel> selectAll() {
        ArrayList<CartModel> carts = new ArrayList<>();
        String sql = "SELECT * FROM carts";
        
        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                carts.add(new CartModel(
                        rs.getInt("id"),
                        rs.getInt("cus_id"),
                        rs.getInt("domain_id"),
                        rs.getInt("years"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carts;
    }

    @Override
    public CartModel selectById(CartModel t) {
        String sql = "SELECT * FROM carts WHERE id=?";
        
        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setInt(1, t.getId());
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new CartModel(
                            rs.getInt("id"),
                            rs.getInt("cus_id"),
                            rs.getInt("domain_id"),
                            rs.getInt("years"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<CartModel> selectByCondition(String condition) {
        ArrayList<CartModel> carts = new ArrayList<>();
        String sql = "SELECT * FROM carts WHERE " + condition;

        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    carts.add(new CartModel(
                            rs.getInt("id"),
                            rs.getInt("cus_id"),
                            rs.getInt("domain_id"),
                            rs.getInt("years"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carts;
    }
    
}

package com.utc2.domainstore.repository;

import com.utc2.domainstore.entity.database.CartModel;
import com.utc2.domainstore.entity.database.DomainModel;
import com.utc2.domainstore.entity.database.DomainStatusEnum;
import com.utc2.domainstore.entity.database.TopLevelDomainModel;
import com.utc2.domainstore.utils.JDBC;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartRepository implements IRepository<CartModel>{
    public static CartRepository getInstance() {
        return new CartRepository();
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
    
    public List<DomainModel> getCartByUserId(int userId) {
        String sql = "SELECT d.id, d.domain_name, d.status, t.price " +
                     "FROM carts c " +
                     "JOIN domains d ON c.domain_id = d.id " +
                     "JOIN TopLevelDomain t ON d.tld_id = t.id " +
                     "WHERE c.cus_id = ?";

        List<DomainModel> domainList = new ArrayList<>();

        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    DomainModel domain = new DomainModel();
                    domain.setId(rs.getInt("id"));
                    domain.setDomainName(rs.getString("domain_name"));
                    domain.setStatus(DomainStatusEnum.valueOf(rs.getString("status")));

                    TopLevelDomainModel tld = new TopLevelDomainModel();
                    tld.setPrice(rs.getInt("price"));
                    domain.setTldId(tld.getId());

                    domainList.add(domain);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return domainList;
    }

    public boolean isDomainAvailable(int domainId) {
        String sql = "SELECT COUNT(*) FROM domains WHERE id = ? AND status = 'AVAILABLE'";
        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, domainId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addToCart(int userId, int domainId, int years) {
        if (!isDomainAvailable(domainId)) {
            System.out.println("Tên miền không khả dụng.");
            return false;
        }

        String sql = "INSERT INTO carts (cus_id, domain_id, years) VALUES (?, ?, ?)";
        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, userId);
            pst.setInt(2, domainId);
            pst.setInt(3, years);

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

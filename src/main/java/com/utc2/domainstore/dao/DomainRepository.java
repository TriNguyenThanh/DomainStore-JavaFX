package com.utc2.domainstore.dao;

import com.utc2.domainstore.entity.database.DomainModel;
import com.utc2.domainstore.entity.database.DomainModel.DomainStatusEnum;
import com.utc2.domainstore.utils.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DomainRepository implements IRepository<DomainModel> {

    public static DomainRepository getInstance() {
        return new DomainRepository();
    }

    @Override
    public int insert(DomainModel domain) {
        String sql = "INSERT INTO domains (domain_name, tld_id, status, active_date, years, owner_id, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())";
        
        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, domain.getDomainName());
            pst.setInt(2, domain.getTldId());
            pst.setString(3, domain.getStatus().name().toLowerCase());
            pst.setDate(4, domain.getActiveDate());
            pst.setInt(5, domain.getYears());

            if (domain.getOwnerId() != null) {
                pst.setInt(6, domain.getOwnerId());
            } else {
                pst.setNull(6, Types.INTEGER);
            }

            int result = pst.executeUpdate();

            // Đóng ResultSet 
            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    domain.setId(rs.getInt(1));
                }
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int update(DomainModel domain) {
        String sql = "UPDATE domains SET domain_name=?, tld_id=?, status=?, active_date=?, years=?, owner_id=? WHERE id=?";
        
        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, domain.getDomainName());
            pst.setInt(2, domain.getTldId());
            pst.setString(3, domain.getStatus().name().toLowerCase());
            pst.setDate(4, domain.getActiveDate());
            pst.setInt(5, domain.getYears());

            if (domain.getOwnerId() != null) {
                pst.setInt(6, domain.getOwnerId());
            } else {
                pst.setNull(6, Types.INTEGER);
            }

            pst.setInt(7, domain.getId());
            return pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int delete(DomainModel domain) {
        String sql = "DELETE FROM domains WHERE id=?";
        
        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, domain.getId());
            return pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public DomainModel selectById(DomainModel domain) {
        String sql = "SELECT * FROM domains WHERE id=?";
        
        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setInt(1, domain.getId());
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new DomainModel(
                            rs.getInt("id"),
                            rs.getString("domain_name"),
                            rs.getInt("tld_id"),
                            DomainStatusEnum.valueOf(rs.getString("status").toUpperCase()),
                            rs.getDate("active_date"),
                            rs.getInt("years"),
                            rs.getObject("owner_id") != null ? rs.getInt("owner_id") : null,
                            rs.getDate("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<DomainModel> selectAll() {
        ArrayList<DomainModel> domains = new ArrayList<>();
        String sql = "SELECT * FROM domains";
        
        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                domains.add(new DomainModel(
                        rs.getInt("id"),
                        rs.getString("domain_name"),
                        rs.getInt("tld_id"),
                        DomainStatusEnum.valueOf(rs.getString("status").toUpperCase()),
                        rs.getDate("active_date"),
                        rs.getInt("years"),
                        rs.getObject("owner_id") != null ? rs.getInt("owner_id") : null,
                        rs.getDate("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return domains;
    }

    @Override
    public ArrayList<DomainModel> selectByCondition(String condition) {
    	ArrayList<DomainModel> domains = new ArrayList<>();
        String sql = "SELECT * FROM domains WHERE " + condition;

        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    domains.add(new DomainModel(
                            rs.getInt("id"),
                            rs.getString("domain_name"),
                            rs.getInt("tld_id"),
                            DomainStatusEnum.valueOf(rs.getString("status").toUpperCase()),
                            rs.getDate("active_date"),
                            rs.getInt("years"),
                            rs.getObject("owner_id") != null ? rs.getInt("owner_id") : null,
                            rs.getDate("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return domains;
    }
    
    //lay ten
    public List<DomainModel> searchByName(String domainInput) {
        List<DomainModel> domainList = new ArrayList<>();
        String domainName = domainInput.contains(".") ? domainInput.split("\\.")[0] : domainInput;
        String query = "SELECT * FROM domains WHERE domain_name LIKE ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, domainName + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    domainList.add(new DomainModel(
                            rs.getInt("id"),
                            rs.getString("domain_name"),
                            rs.getInt("tld_id"),
                            DomainModel.DomainStatusEnum.valueOf(rs.getString("status").toUpperCase()),
                            rs.getDate("active_date"),
                            rs.getInt("years"),
                            rs.getObject("owner_id") != null ? rs.getInt("owner_id") : null,
                            rs.getDate("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return domainList;
    }

    //lay danh sach theo userId
    public List<DomainModel> getCartByUserId(int userId) {
        List<DomainModel> cartList = new ArrayList<>();
        String query = "SELECT * FROM domains WHERE owner_id = ? AND status = 'available'";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cartList.add(new DomainModel(
                            rs.getInt("id"),
                            rs.getString("domain_name"),
                            rs.getInt("tld_id"),
                            DomainModel.DomainStatusEnum.valueOf(rs.getString("status").toUpperCase()),
                            rs.getDate("active_date"),
                            rs.getInt("years"),
                            rs.getInt("owner_id")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartList;
    }

    //them vao gio hang
    public boolean updateDomainOwnership(int userId, DomainModel domain) {
        String updateQuery = "UPDATE domains SET owner_id = ?, years = ? WHERE id = ? AND status = 'available'";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            updateStmt.setInt(1, userId);
            updateStmt.setInt(2, domain.getYears());
            updateStmt.setInt(3, domain.getId());

            return updateStmt.executeUpdate() > 0; 
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

package com.utc2.domainstore.repository;

import com.utc2.domainstore.entity.database.DomainModel;
import com.utc2.domainstore.entity.database.DomainStatusEnum;
import com.utc2.domainstore.config.JDBC;
import com.utc2.domainstore.entity.database.DomainWithTldModel;
import com.utc2.domainstore.entity.database.TopLevelDomainModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DomainRepository implements IRepository<DomainModel> {

    public static DomainRepository getInstance() {
        return new DomainRepository();
    }

    @Override
    public int insert(DomainModel domain) {
        String sql = "INSERT INTO domains (domain_name, tld_id, status, active_date, years, price, owner_id, created_at) VALUES (?, ?, ?, ?, ?, ?, ?,NOW())";

        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, domain.getDomainName());
            pst.setInt(2, domain.getTldId());
            pst.setString(3, domain.getStatus().name().toLowerCase());
            pst.setTimestamp(4, domain.getActiveDate());
            pst.setInt(5, domain.getYears());
            pst.setLong(6, domain.getPrice());
            if (domain.getOwnerId() != null) {
                pst.setInt(7, domain.getOwnerId());
            } else {
                pst.setNull(7, Types.INTEGER);
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
        String sql = "UPDATE domains SET domain_name=?, tld_id=?, status=?, active_date=?, years=?, price=?, owner_id=? WHERE id=?";

        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, domain.getDomainName());
            pst.setInt(2, domain.getTldId());
            pst.setString(3, domain.getStatus().name().toLowerCase());
            pst.setTimestamp(4, domain.getActiveDate());
            pst.setInt(5, domain.getYears());
            pst.setLong(6, domain.getPrice());
            if (domain.getOwnerId() != null) {
                pst.setInt(7, domain.getOwnerId());
            } else {
                pst.setNull(7, Types.INTEGER);
            }

            pst.setInt(8, domain.getId());
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
                            rs.getTimestamp("active_date"),
                            rs.getInt("years"),
                            rs.getLong("price"),
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
                        rs.getTimestamp("active_date"),
                        rs.getInt("years"),
                        rs.getLong("price"),
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
                            rs.getTimestamp("active_date"),
                            rs.getInt("years"),
                            rs.getLong("price"),
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
                            DomainStatusEnum.valueOf(rs.getString("status").toUpperCase()),
                            rs.getTimestamp("active_date"),
                            rs.getInt("years"),
                            rs.getLong("price"),
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

    public boolean isDomainExists(String nameDomain, int tld_id) {
        String domainName = nameDomain.contains(".") ? nameDomain.split("\\.")[0] : nameDomain;
        String sql = "SELECT * FROM domains WHERE domain_name = ? AND tld_id = ?";

        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, domainName);
            pst.setInt(2, tld_id);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public DomainModel getDomainByNameAndTld(String nameDomain, int tld_id) {
        String domainName = nameDomain.contains(".") ? nameDomain.split("\\.")[0] : nameDomain;
        String sql = "SELECT * FROM domains WHERE domain_name = ? AND tld_id = ?";

        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, domainName);
            pst.setInt(2, tld_id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new DomainModel(
                            rs.getInt("id"),
                            rs.getString("domain_name"),
                            rs.getInt("tld_id"),
                            DomainStatusEnum.valueOf(rs.getString("status").toUpperCase()),
                            rs.getTimestamp("active_date"),
                            rs.getInt("years"),
                            rs.getLong("price"),
                            rs.getInt("owner_id"),
                            rs.getDate("created_at")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
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
                            DomainStatusEnum.valueOf(rs.getString("status").toUpperCase()),
                            rs.getTimestamp("active_date"),
                            rs.getInt("years"),
                            rs.getLong("price"),
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

    // Lấy giá của TLD dựa trên domain_id
    public Integer getTLDPriceByDomainId(int domainId) {
        String sql = "SELECT t.price FROM domains d " +
                "JOIN TopLevelDomain t ON d.tld_id = t.id " +
                "WHERE d.id = ?";
        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, domainId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("price");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Integer getTLDPriceByTldId(int tldId) {
        String sql = "SELECT price FROM TopLevelDomain WHERE id = ?";
        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, tldId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("price");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //lấy tên miền ngẫu nhiên
    public List<DomainModel> getSuggestedDomains(int limit){
        List<DomainModel> domainList = new ArrayList<>();
        String sql = "SELECT * FROM domains WHERE status = 'available' ORDER BY RAND() LIMIT " + limit;
        try (Connection conn = JDBC.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    domainList.add(new DomainModel(
                            rs.getInt("id"),
                            rs.getString("domain_name"),
                            rs.getInt("tld_id"),
                            DomainStatusEnum.valueOf(rs.getString("status").toUpperCase()),
                            rs.getTimestamp("active_date"),
                            rs.getInt("years"),
                            rs.getLong("price"),
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
    //lấy những tên miền đã mua của 1 người nào đó
    public List<DomainModel> getSoldDomains(int user_id){
        List<DomainModel> domainList = new ArrayList<>();
        String sql = "SELECT * FROM domains WHERE status = 'sold' and owner_id = ?";
        try (Connection conn = JDBC.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user_id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    domainList.add(new DomainModel(
                            rs.getInt("id"),
                            rs.getString("domain_name"),
                            rs.getInt("tld_id"),
                            DomainStatusEnum.valueOf(rs.getString("status").toUpperCase()),
                            rs.getTimestamp("active_date"),
                            rs.getInt("years"),
                            rs.getLong("price"),
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
    public int updateByStatusAndTldId(Long price, int tld_id) {
        String sql = "UPDATE domains SET price=? WHERE status='available' and tld_id=?";

        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setLong(1, price);
            pst.setInt(2, tld_id);

            return pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public List<DomainWithTldModel> selectAllDomainWithTld(){
        List<DomainWithTldModel> result = new ArrayList<>();
        String sql = "SELECT d.id, d.domain_name, d.tld_id, d.status, d.years, " +
                "d.price AS domain_price, d.active_date, d.owner_id, " +
                "t.id AS tld_id, t.TLD_text, t.price AS tld_price " +
                "FROM domains d " +
                "JOIN TopLevelDomain t ON d.tld_id = t.id";
        try(Connection con = JDBC.getConnection();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()){
            while(rs.next()){
                DomainModel domain = new DomainModel();
                domain.setId(rs.getInt("id"));
                domain.setDomainName(rs.getString("domain_name"));
                domain.setTldId(rs.getInt("tld_id"));
                domain.setStatus(DomainStatusEnum.valueOf(rs.getString("status").toUpperCase()));
                domain.setYears(rs.getInt("years"));
                domain.setPrice(rs.getLong("domain_price"));
                domain.setActiveDate(rs.getTimestamp("active_date") != null ? rs.getTimestamp("active_date") : null);
                domain.setOwnerId(rs.getObject("owner_id") != null ? rs.getInt("owner_id") : null);

                TopLevelDomainModel tld = new TopLevelDomainModel();
                tld.setId(rs.getInt("tld_id"));
                tld.setTldText(rs.getString("TLD_text"));
                tld.setPrice(rs.getLong("tld_price"));

                result.add(new DomainWithTldModel(domain,tld));
            }
        }   catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }
}

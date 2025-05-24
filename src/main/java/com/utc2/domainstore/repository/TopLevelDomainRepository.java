package com.utc2.domainstore.repository;

import com.utc2.domainstore.entity.database.TopLevelDomainModel;
import com.utc2.domainstore.config.JDBC;

import java.sql.*;
import java.util.ArrayList;

public class TopLevelDomainRepository implements IRepository<TopLevelDomainModel> {
    public static TopLevelDomainRepository getInstance() {
        return new TopLevelDomainRepository();
    }

    @Override
    public int insert(TopLevelDomainModel t) {
        String query = "INSERT INTO TopLevelDomain (TLD_text, price) VALUES (?, ?)";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, t.getTldText());
            stmt.setLong(2, t.getPrice());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int update(TopLevelDomainModel t) {
        String query = "UPDATE TopLevelDomain SET TLD_text = ?, price = ? WHERE id = ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, t.getTldText());
            stmt.setLong(2, t.getPrice());
            stmt.setInt(3, t.getId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int delete(TopLevelDomainModel t) {
        String query = "DELETE FROM TopLevelDomain WHERE id = ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, t.getId());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public ArrayList<TopLevelDomainModel> selectAll() {
        ArrayList<TopLevelDomainModel> list = new ArrayList<>();
        String query = "SELECT * FROM TopLevelDomain";

        try (Connection conn = JDBC.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(new TopLevelDomainModel(
                        rs.getInt("id"),
                        rs.getString("TLD_text"),
                        rs.getLong("price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public TopLevelDomainModel selectById(TopLevelDomainModel t) {
        String query = "SELECT * FROM TopLevelDomain WHERE id = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, t.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TopLevelDomainModel(
                            rs.getInt("id"),
                            rs.getString("TLD_text"),
                            rs.getLong("price")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<TopLevelDomainModel> selectByCondition(String condition) {
        ArrayList<TopLevelDomainModel> list = new ArrayList<>();
        String query = "SELECT * FROM TopLevelDomain WHERE " + condition;

        try (Connection conn = JDBC.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                list.add(new TopLevelDomainModel(
                        rs.getInt("id"),
                        rs.getString("TLD_text"),
                        rs.getLong("price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public TopLevelDomainModel getTLDByName(String tldText) {
     String query = "SELECT * FROM TopLevelDomain WHERE TLD_text = ?";

     try (Connection conn = JDBC.getConnection();
          PreparedStatement stmt = conn.prepareStatement(query)) {

         stmt.setString(1, tldText);
         try (ResultSet rs = stmt.executeQuery()) {
             if (rs.next()) {
                 return new TopLevelDomainModel(
                         rs.getInt("id"),
                         rs.getString("TLD_text"),
                         rs.getLong("price")
                 );
             }
         }
     } catch (SQLException e) {
         e.printStackTrace();
     }
     return null;
    }
}

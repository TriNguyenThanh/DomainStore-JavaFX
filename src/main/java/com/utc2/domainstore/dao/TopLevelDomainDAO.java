package com.utc2.domainstore.dao;

import com.utc2.domainstore.model.TopLevelDomainModel;
import com.utc2.domainstore.utils.JDBC;

import java.sql.*;
import java.util.ArrayList;

public class TopLevelDomainDAO implements DAOInterface<TopLevelDomainModel> {
    public static TopLevelDomainDAO getInstance() {
        return new TopLevelDomainDAO();
    }

    @Override
    public int insert(TopLevelDomainModel t) {
        String query = "INSERT INTO TopLevelDomain (TLD_text, price) VALUES (?, ?)";
        Connection conn = JDBC.getConnection();
        if (conn == null) return -1;

        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, t.getTldText());
            stmt.setInt(2, t.getPrice());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int update(TopLevelDomainModel t) {
        String query = "UPDATE TopLevelDomain SET TLD_text = ?, price = ? WHERE id = ?";
        Connection conn = JDBC.getConnection();
        if (conn == null) return 0;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, t.getTldText());
            stmt.setInt(2, t.getPrice());
            stmt.setInt(3, t.getId());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } 
        return 0;
    }

    @Override
    public int delete(TopLevelDomainModel t) {
        String query = "DELETE FROM TopLevelDomain WHERE id = ?";
        Connection conn = JDBC.getConnection();
        if (conn == null) return 0;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, t.getId());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public ArrayList<TopLevelDomainModel> selectAll() {
        ArrayList<TopLevelDomainModel> list = new ArrayList<>();
        String query = "SELECT * FROM TopLevelDomain";
        Connection conn = JDBC.getConnection();
        if (conn == null) return list;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new TopLevelDomainModel(
                        rs.getInt("id"),
                        rs.getString("TLD_text"),
                        rs.getInt("price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public TopLevelDomainModel selectById(TopLevelDomainModel t) {
        String query = "SELECT * FROM TopLevelDomain WHERE id = ?";
        Connection conn = JDBC.getConnection();
        if (conn == null) return null;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, t.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new TopLevelDomainModel(
                        rs.getInt("id"),
                        rs.getString("TLD_text"),
                        rs.getInt("price")
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
    public ArrayList<TopLevelDomainModel> selectByCondition(String condition) {
        ArrayList<TopLevelDomainModel> list = new ArrayList<>();
        String query = "SELECT * FROM TopLevelDomain WHERE " + condition;
        Connection conn = JDBC.getConnection();
        if (conn == null) return list;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new TopLevelDomainModel(
                        rs.getInt("id"),
                        rs.getString("TLD_text"),
                        rs.getInt("price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } 
        return list;
    }


}

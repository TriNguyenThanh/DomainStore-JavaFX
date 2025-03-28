package com.utc2.domainstore.utils;

import com.utc2.domainstore.view.ConfigManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBC {
    public static Connection getConnection() {
        Connection c = null;
        try {
            try {
                Class.forName(ConfigManager.getInstance().getSetting("JDBC.driver", "com.mysql.cj.jdbc.Driver"));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("no found driver");
            }
            String url = ConfigManager.getInstance().getSetting("JDBC.url", "jdbc:mysql://localhost:3306/domainmanagement");
            String username = ConfigManager.getInstance().getSetting("JDBC.username", "root");
            String password = ConfigManager.getInstance().getSetting("JDBC.password", "root");
            
            c = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("no found connection");
        }
        return c;
    }

    public static void closeConnection(Connection c) {
        try {
            if (c != null)
                c.close();
        } catch (SQLException e) {
            throw new RuntimeException("can't close connection");
        }
    }
}

package com.utc2.domainstore.service;

import com.utc2.domainstore.utils.EmailUtil;
import com.utc2.domainstore.utils.EmailUtil;
import com.utc2.domainstore.config.JDBC;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;

public class DomainNotifierServices {
    public JSONObject notifyExpiringDomains() {
        String sql = "SELECT email, active_date, domains.years, domain_name, TLD_text " +
                "FROM domains JOIN users ON domains.owner_id = users.id JOIN topleveldomain ON domains.id = topleveldomain.id " +
                "WHERE DATE_ADD(active_date, INTERVAL years YEAR) <= NOW() + INTERVAL 7 DAY " +
                "AND DATE_ADD(active_date, INTERVAL years YEAR) > NOW()";

        JSONObject response = new JSONObject();
        int count = 0;

        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Timestamp activeDate = rs.getTimestamp("active_date");
                int years = rs.getInt("years");
                String domain = rs.getString("domain_name");
                String tldText = rs.getString("TLD_text");
                String email = rs.getString("email");
                String fullNameDomain = domain + tldText;
                if (!email.contains("@")) {
                    email += "@gmail.com";
                }

                LocalDate expiredDate = activeDate.toLocalDateTime().toLocalDate().plusYears(years);
                String subject = "Tên miền sắp hết hạn: " + domain;
                String content = "Tên miền \"" + fullNameDomain + "\" sẽ hết hạn vào ngày " + expiredDate +
                        ". Vui lòng gia hạn để không bị mất quyền sử dụng.";

                EmailUtil.sendEmail(email, subject, content);
                count++;
            }

            if (count > 0) {
                response.put("status", "success");
                response.put("message", "Send successfully");
                response.put("count", count);
            } else {
                response.put("status", "empty");
                response.put("message", "No expiring domains found");
                response.put("count", 0);
            }

        } catch (SQLException e) {
            response.put("status", "error");
            response.put("message", "Error sending email: " + e.getMessage());
        }

        return response;
    }
    public JSONObject getExpiringDomainsAsJson() {
        String sql = "SELECT email, active_date, domains.years, domain_name, TLD_text, " +
                "DATE_ADD(active_date, INTERVAL years YEAR) AS expired_date " +
                "FROM domains " +
                "JOIN users ON domains.owner_id = users.id " +
                "INNER JOIN topleveldomain ON domains.id = topleveldomain.id " +
                "WHERE DATE_ADD(active_date, INTERVAL years YEAR) <= NOW() + INTERVAL 7 DAY " +
                "AND DATE_ADD(active_date, INTERVAL years YEAR) > NOW()";
        JSONObject response = new JSONObject();
        JSONArray domainArray = new JSONArray();

        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                JSONObject domainJson = new JSONObject();

                Timestamp activeDate = rs.getTimestamp("active_date");
                int years = rs.getInt("years");
                String domainName = rs.getString("domain_name");
                String tldText = rs.getString("TLD_text");
                String email = rs.getString("email");
                String fullNameDomain = domainName + tldText;
                LocalDate expiredDate = activeDate.toLocalDateTime().toLocalDate().plusYears(years);

                domainJson.put("email", email);
                domainJson.put("active_date", activeDate.toLocalDateTime().toLocalDate().toString());
                domainJson.put("years", years);
                domainJson.put("domain_name", fullNameDomain);
                domainJson.put("expired_date", expiredDate.toString());

                domainArray.put(domainJson);
            }

            response.put("status", "success");
            response.put("domains", domainArray);

        } catch (SQLException e) {
            response.put("status", "error");
            response.put("message", "Lỗi truy vấn CSDL: " + e.getMessage());
        }

        return response;
    }
}

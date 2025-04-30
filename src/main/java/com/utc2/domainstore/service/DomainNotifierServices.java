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

public class DomainNotifierServices {
    public void notifyExpiringDomains() {
        String sql = "SELECT email, active_date, domains.years, domain_name FROM domains join users ON domains.owner_id = users.id " +
                "WHERE DATE_ADD(active_date, INTERVAL years YEAR) <= NOW() + INTERVAL 7 DAY " +
                "AND DATE_ADD(active_date, INTERVAL years YEAR) > NOW()";

        try (Connection con = JDBC.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Timestamp activeDate = rs.getTimestamp("active_date");
                int years = rs.getInt("years");
                String domain = rs.getString("domain_name");
                String email = rs.getString("email");

                if (!email.contains("@")) {
                    email += "@gmail.com"; // Tạm xử lý nếu thiếu đuôi
                }

                LocalDate expiredDate = activeDate.toLocalDateTime().toLocalDate().plusYears(years);
                String subject = "Tên miền sắp hết hạn: " + domain;
                String content = "Tên miền \"" + domain + "\" sẽ hết hạn vào ngày " + expiredDate
                        + ". Vui lòng gia hạn để không bị mất quyền sử dụng.";

                EmailUtil.sendEmail(email, subject, content);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class DomainNotifierServices implements IDomainNotifier{
    public JSONObject notifyExpiringDomains(JSONArray jsonInput) {
        JSONObject response = new JSONObject();
        int count = 0;

        // Map gom email -> danh sách domain hết hạn
        Map<String, List<String>> emailToDomains = new HashMap<>();

        for (int i = 0; i < jsonInput.length(); i++) {
            JSONObject item = jsonInput.getJSONObject(i);
            String email = item.getString("email");
            String domain = item.getString("domain_name");
            String expiredDate = item.getString("expired_date");
            String fullDomain = domain + " (hết hạn: " + expiredDate + ")";

            if (!email.contains("@")) {
                email += "@gmail.com";
            }

            emailToDomains
                    .computeIfAbsent(email, k -> new ArrayList<>())
                    .add(fullDomain);
        }

        // Gửi 1 email cho mỗi người
        for (Map.Entry<String, List<String>> entry : emailToDomains.entrySet()) {
            String email = entry.getKey();
            List<String> domains = entry.getValue();

            StringBuilder contentBuilder = new StringBuilder();
            contentBuilder.append("Chào bạn,\n\nCác tên miền sau đây của bạn sắp hết hạn:\n");

            for (String domain : domains) {
                contentBuilder.append("- ").append(domain).append("\n");
            }

            contentBuilder.append("\nVui lòng gia hạn để không bị mất quyền sử dụng.\n\nTrân trọng.");

            String subject = "Thông báo: " + domains.size() + " tên miền sắp hết hạn";
            EmailUtil.sendEmail(email, subject, contentBuilder.toString());
            count++;
        }

        response.put("status", "success");
        response.put("message", "Đã gửi email đến " + count + " người dùng");
        response.put("count", count);
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

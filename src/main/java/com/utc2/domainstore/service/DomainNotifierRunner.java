package com.utc2.domainstore.service;

import com.utc2.domainstore.service.DomainNotifierServices;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DomainNotifierRunner {
    public static void main(String[] args) {
        DomainNotifierServices notifierService = new DomainNotifierServices();

        // Lấy danh sách domain sắp hết hạn từ CSDL
        JSONObject expiringDomainsResponse = notifierService.getExpiringDomainsAsJson();

        if (expiringDomainsResponse.getString("status").equals("success")) {
            JSONArray domainArray = expiringDomainsResponse.getJSONArray("domains");

            // Gửi email thông báo đến các người dùng
            JSONObject notifyResult = notifierService.notifyExpiringDomains(domainArray);

            System.out.println("Kết quả gửi email:");
            System.out.println(notifyResult.toString(2));
            // Lấy thời gian hiện tại và định dạng
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            System.out.println("Thời gian gửi email: " + now.format(formatter));
            System.out.println("============================================================");
        } else {
            System.err.println("Lỗi khi lấy dữ liệu domain: " +
                    expiringDomainsResponse.getString("message"));
        }
    }
}

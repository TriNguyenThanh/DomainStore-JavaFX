package com.utc2.domainstore.service;

import com.utc2.domainstore.utils.EmailUtil;
import java.util.List;

public class SoldDomainNotifierServices {
    public void notifySoldDomains(String email, List<String> domains) {
        if (!email.contains("@")) {
            email += "@gmail.com"; // Tạm xử lý nếu thiếu đuôi
        }

        String subject = "Thông báo về các tên miền bạn đang sở hữu";

        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("Chào bạn,\n\n");
        contentBuilder.append("Các tên miền sau đây đã được ghi nhận là đã bán thành công:\n");

        for (String domain : domains) {
            contentBuilder.append("- ").append(domain).append("\n");
        }

        contentBuilder.append("\nCảm ơn bạn đã sử dụng dịch vụ của chúng tôi!\n\nTrân trọng.");

        EmailUtil.sendEmail(email, subject, contentBuilder.toString());
    }
}

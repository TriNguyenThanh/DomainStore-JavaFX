package com.utc2.domainstore.service;

import com.utc2.domainstore.utils.EmailUtil;

public class SoldDomainNotifierServices {
    public void notifySoldDomain(String email, String domain) {
        if (!email.contains("@")) {
            email += "@gmail.com"; // Tạm xử lý nếu thiếu đuôi
        }

        String subject = "Thông báo về tên miền bạn đang sở hữu";
        String content = "Chào bạn,\n\nTên miền \"" + domain + "\" đang được ghi nhận là đã bán thành công." +
                "\nCảm ơn bạn đã sử dụng dịch vụ của chúng tôi!\n\nTrân trọng.";

        EmailUtil.sendEmail(email, subject, content);
    }
}

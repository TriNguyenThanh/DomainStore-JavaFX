package com.utc2.domainstore.utils;

import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class DomainUtils {

    private static final String RDAP_API_URL = "https://rdap.org/domain/";

    public static String getDomainInfo(String domain) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(RDAP_API_URL + domain)
                .build();

        try (Response response = client.newCall(request).execute()) {

            // return "Nofound" if domain isn't exist
            if (response.code() == 404) {
                return "Nofound";
            }

            if (!response.isSuccessful()) {
                return "Error";
            }

            // Chuyển đổi JSON response thành chuỗi
            String jsonData = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonData);

            // Lấy một số thông tin quan trọng
            String domainName = jsonObject.optString("ldhName", "Không có dữ liệu");
            String handle = jsonObject.optString("handle", "Không có dữ liệu");

            // Lấy trạng thái domain
            String status = jsonObject.has("status") ? jsonObject.getJSONArray("status").join(", ") : "Không có dữ liệu";

            // Lấy ngày hết hạn
            String expirationDate = "Không có dữ liệu";
            if (jsonObject.has("events")) {
                for (Object event : jsonObject.getJSONArray("events")) {
                    JSONObject eventObj = (JSONObject) event;
                    if ("expiration".equals(eventObj.optString("eventAction"))) {
                        expirationDate = eventObj.optString("eventDate");
                        break;
                    }
                }
            }

            return "Tên miền: " + domainName + "\nMã định danh: " + handle
                    + "\nTrạng thái: " + status + "\nNgày hết hạn: " + expirationDate;
        } catch (IOException e) {
            return "Lỗi kết nối: " + e.getMessage();
        }
    }
}

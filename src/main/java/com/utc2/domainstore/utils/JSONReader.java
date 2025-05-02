package com.utc2.domainstore.utils;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JSONReader {
    public static JSONObject loadJSON(String filePath) {

        try (InputStream inputStream = JSONReader.class.getResourceAsStream(filePath)) {
            if (inputStream == null) {
                System.err.println("Không tìm thấy file: " + filePath);
                return null;
            }
            String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            return new JSONObject(content);

        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file: " + e.getMessage());
        } catch (org.json.JSONException e) {
            System.err.println("Lỗi khi parse JSON: " + e.getMessage());
        }

        return null;
    }
}
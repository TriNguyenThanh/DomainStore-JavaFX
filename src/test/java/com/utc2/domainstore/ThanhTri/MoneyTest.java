package com.utc2.domainstore.ThanhTri;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MoneyTest {
    public static void main(String[] args) {
        try {
            String amount = "100";
            String from = "USD";
            String to = "VND";

            String urlStr = String.format("https://api.frankfurter.app/latest?amount=%s&from=%s&to=%s", amount, from, to);
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            conn.disconnect();

            // Parse JSON
            JSONObject obj = new JSONObject(content.toString());
            double result = obj.getJSONObject("rates").getDouble(to);

            System.out.printf("%s %s = %.2f %s%n", amount, from, result, to);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

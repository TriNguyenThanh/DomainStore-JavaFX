package com.utc2.domainstore.ThanhTri;


import com.utc2.domainstore.view.ConfigManager;

import java.time.LocalDateTime;

public class Test {
    public static void main(String[] args) {
        LocalDateTime date = LocalDateTime.parse("2024-01-21 07:03:00.9", ConfigManager.getInstance().getParser());
        System.out.println(date.format(ConfigManager.getInstance().getDateTimeFormatter()));
    }
}
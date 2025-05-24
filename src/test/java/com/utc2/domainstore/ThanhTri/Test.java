package com.utc2.domainstore.ThanhTri;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Test {
    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
        System.out.println(now.format(outputFormat));
    }
}
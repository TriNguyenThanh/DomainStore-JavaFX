package com.utc2.domainstore.utils;

//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
//import java.util.Base64;
//
//public class PasswordUtils {
//
//    // Tạo salt ngẫu nhiên
//    public static String generateSalt() {
//        SecureRandom random = new SecureRandom();
//        byte[] salt = new byte[16]; // 16 bytes (128 bits) salt
//        random.nextBytes(salt);
//        return Base64.getEncoder().encodeToString(salt);
//    }
//
//    // Băm mật khẩu với salt bằng SHA-256
//    public static String hashedPassword(String password, String salt) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-256");
//            md.update(salt.getBytes()); // Kết hợp salt với mật khẩu
//            byte[] hashedBytes = md.digest(password.getBytes());
//
//            return Base64.getEncoder().encodeToString(hashedBytes);
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("Lỗi khi băm mật khẩu", e);
//        }
//    }
//
//    // Kiểm tra mật khẩu nhập vào với hash, salt đã lưu trong database
//    public static boolean verifyPassword(String inputPassword, String storedHash, String salt) {
//        String newHash = hashedPassword(inputPassword, salt);
//        return newHash.equals(storedHash);
//    }
//}
import de.mkammerer.argon2.Argon2;

import de.mkammerer.argon2.Argon2Factory;

public class PasswordUtils {

    public static void main(String[] args) {
        System.out.println("Pass: pass123456@");
        System.out.println(hashedPassword("pass123456@"));
    }

    public static String hashedPassword(String password) {

        // Tạo instance của Argon2
        Argon2 argon2 = Argon2Factory.create();
        String hashedPassword;
        try {
            // Hash mật khẩu (với thời gian xử lý ~64MB RAM, 3 vòng lặp, 1 luồng)
            hashedPassword = argon2.hash(3, 65536, 1, password);

            // Xác thực mật khẩu nhập vào với hash đã lưu
            boolean isMatched = argon2.verify(hashedPassword, password);
            System.out.println("Password Match: " + isMatched);
        } finally {
            // Giải phóng bộ nhớ khi không dùng nữa
            argon2.wipeArray(password.toCharArray());
        }

        return hashedPassword;
    }
}

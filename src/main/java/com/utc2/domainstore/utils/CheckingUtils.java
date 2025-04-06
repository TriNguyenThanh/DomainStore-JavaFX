package com.utc2.domainstore.utils;

public class CheckingUtils {
    // sdt phải bắt đầu bằng 0 hoặc +84 và có độ dài là 9
    public static boolean phoneNumberCheck(String s) {
        String pattern = "^(0|\\+84)\\d{9}$";
        return s.matches(pattern);
    }

    // kiểm tra số căn cước công dân
    public static boolean personalIDCheck(String s) {
        String pattern = "^(0|)\\d{12}$";
        return s.matches(pattern);
    }

    // kiểm tra email
    public static boolean emailCheck(String s) {
        String pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return s.matches(pattern);
    }

    //kiểm tra mặt khẩu có độ dài 8-16 ký tự, bao gồm a - z, A - Z, 0 - 9, @, _, .
    public static boolean passwordCheck(String s) {
        String pattern = "^[a-zA-Z0-9@_]{8,16}$";
        return s.matches(pattern);
    }
}

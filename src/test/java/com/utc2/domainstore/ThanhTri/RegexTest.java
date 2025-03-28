package com.utc2.domainstore.ThanhTri;

public class RegexTest {
    private static boolean emailCheck(String s) {
        String pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return s.matches(pattern);
    }

    public static void main(String[] args) {
        System.out.println(emailCheck("aaaaa"));
    }
}

package com.caih.cloud.iscs.charge.utils;

import java.util.regex.Pattern;

public class EStringUtils {

    public static void main(String[] args) {
        System.out.println("isNumeric(\"aa\") = " + isDouble("0.01"));
    }

    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        } else {
            int sz = str.length();

            for (int i = 0; i < sz; ++i) {
                if (!Character.isDigit(str.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }


    public static boolean isDouble(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        // 之前这里正则表达式错误，现更正
        Pattern pattern = Pattern.compile("^[-\\+]?\\d*[.]\\d+$");
        return pattern.matcher(str).matches();
    }

}

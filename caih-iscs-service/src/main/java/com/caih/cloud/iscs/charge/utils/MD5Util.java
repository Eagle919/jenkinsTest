package com.caih.cloud.iscs.charge.utils;

import java.security.MessageDigest;

public class MD5Util {
    private static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String makeMD5(String msg)
    {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            return "";
        }
        byte[] digest = md5.digest(msg.getBytes());
        char str[] = new char[32];
        for (int i = 0, k = 0; i < 16; i++) {
            byte byte0 = digest[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }

}

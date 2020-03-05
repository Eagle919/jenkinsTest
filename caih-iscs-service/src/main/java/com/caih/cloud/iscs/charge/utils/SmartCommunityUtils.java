package com.caih.cloud.iscs.charge.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SmartCommunityUtils {

    /**
     * md5加密产生，产生128位（bit）的mac
     * 将128bit Mac转换成16进制代码
     *
     * @param strSrc
     * @param key
     * @return
     */
    public static String MD5EncodeFor16(String strSrc, String key) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(strSrc.getBytes(StandardCharsets.UTF_8));

            String result = "";
            byte[] temp;
            temp = md5.digest(key.getBytes(StandardCharsets.UTF_8));

            for (byte b : temp) {
                result += Integer.toHexString(
                        (0x000000ff & b) | 0xffffff00).substring(6);
            }

            return result;

        } catch (Exception e) {

            e.printStackTrace();

        }
        return null;
    }
}

package com.github.pig.common.util.sherry;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class StringUtils {
    public static void main(String[] args) {
        String randomCharAndNumr = getRandomCharAndNumr(8);
        System.out.println("randomCharAndNumr = " + randomCharAndNumr);
    }
    /**
     * 获取随机字母数字组合
     *
     * @param length 字符串长度
     */
    public static String getRandomCharAndNumr(Integer length) {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            boolean b = random.nextBoolean();
            if (b) { // 字符串
                // int choice = random.nextBoolean() ? 65 : 97; 取得65大写字母还是97小写字母
                str.append((char) (65 + random.nextInt(26)));// 取得大写字母
            } else { // 数字
                str.append(random.nextInt(10));
            }
        }
        return str.toString();
    }

    /**
     * 签名
     *
     * @param params 封装的参数
     * @param appkey 应用密钥
     * @return 字符串
     * @throws Exception 异常
     */
    public static String sign(Map<String,String> params, String appkey) throws Exception {
        TreeMap<String, String> treeMap = new TreeMap<>();
        treeMap.putAll(params);
        StringBuilder sb = new StringBuilder();
        treeMap.put("key", appkey);
        for (Map.Entry<String, String> entry : treeMap.entrySet()) {
            if (entry.getValue() != null && entry.getValue().length() > 0) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        String sign = md5(sb.toString().getBytes(StandardCharsets.UTF_8));//记得是md5编码的加签
        treeMap.remove("key");
        return sign;
    }

    /**
     * md5
     *
     * @param b 字节
     * @return String
     */
    private static String md5(byte[] b) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(b);
            byte[] hash = md.digest();
            StringBuilder outStrBuf = new StringBuilder(32);
            for (byte h : hash) {
                int v = h & 0xFF;
                if (v < 16) {
                    outStrBuf.append('0');
                }
                outStrBuf.append(Integer.toString(v, 16).toLowerCase());
            }
            return outStrBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new String(b);
        }
    }

}

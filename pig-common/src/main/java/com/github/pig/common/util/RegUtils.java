package com.github.pig.common.util;

import com.github.pig.common.constant.EnumConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @className RegUtils
 * @Author chenkang
 * @Date 2019/8/20 14:56
 * @Version 1.0
 */
public class RegUtils {


    /**
     * 正则校验手机
     * @param tel
     * @return
     */
    public static boolean validateTel(String tel){
        return match(EnumConstants.Reg.PHONE_NUM.getRegex(),tel);
    }


    /**
     * 验证方法
     * @param regex
     * @param str
     * @return
     */
    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}

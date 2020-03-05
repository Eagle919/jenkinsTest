package com.github.pig.common.util;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;

/**
 * @author yuwei
 * @date 2018/12/3 20:03
 */
public class StringUtil extends StringUtils {
    private StringUtil() {}
    /**
     * 在之前加0
     * @param num 要加0的数字
     * @param pattern 样例，例如"00000"
     * @return
     */
    public static String addZeroBefore(final Integer num, final String pattern) {
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(num);
    }
}

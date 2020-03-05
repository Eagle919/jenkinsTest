package com.caih.cloud.iscs.charge.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class EAliPayUtils {
    /**
     * 除法
     */
    public static BigDecimal divide(String arg1, String arg2) {
        if (StringUtils.isEmpty(arg1)) {
            arg1 = "0.0";
        }
        if (StringUtils.isEmpty(arg2)) {
            arg2 = "0.0";
        }
        BigDecimal big3 = new BigDecimal("0.00");
        if (Double.parseDouble(arg2) != 0) {
            BigDecimal big1 = new BigDecimal(arg1);
            BigDecimal big2 = new BigDecimal(arg2);
            big3 = big1.divide(big2, 2, BigDecimal.ROUND_HALF_EVEN);
        }
        return big3;
    }
}

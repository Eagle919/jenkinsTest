package com.caih.cloud.iscs.charge.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CentToDisplay {

    public static String convert(Integer cent)
    {
        int scale = cent % 100 == 0 ? 0 : 2;
        return BigDecimal.valueOf(cent).divide(BigDecimal.valueOf(100), scale, RoundingMode.CEILING).toPlainString();
    }
}

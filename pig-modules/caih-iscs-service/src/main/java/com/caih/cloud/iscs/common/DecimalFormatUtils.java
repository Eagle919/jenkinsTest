package com.caih.cloud.iscs.common;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class DecimalFormatUtils {
    public static String formatBigDecimal(BigDecimal needFormatData, String format) {
        if (format == null) format = "###,##0.00";
        DecimalFormat decimalFormat = new DecimalFormat(format);
        return decimalFormat.format(needFormatData);
    }
}

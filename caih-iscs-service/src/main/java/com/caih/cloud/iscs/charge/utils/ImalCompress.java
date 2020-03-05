package com.caih.cloud.iscs.charge.utils;

import com.caih.cloud.iscs.charge.em.PayWay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ImalCompress {
    private static char[] symbols = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y'};

    private static Map<Character, Long> symbolValues = new HashMap<>();

    static {
        for (long i = 0L; i < symbols.length; ++i)
            symbolValues.put(symbols[(int) i], i);
    }

    private static long last = 0;

    private synchronized static long sysCurMil() {
        long now = System.currentTimeMillis();
        if (last >= now)
            return ++last;
        return last = now;
    }

    public static String compress(Integer value) {
        return compress((long) value);
    }

    public static Integer deCompressInt(String str) {
        return deCompress(str).intValue();
    }

    public static String compress(Long value) {
        if (value == 0)
            return "a";
        Stack<Character> stack = new Stack<>();
        while (value > 0) {
            stack.push(symbols[(int) (value % 60)]);
            value /= 60;
        }
        StringBuilder builder = new StringBuilder();
        while (!stack.isEmpty())
            builder.append(stack.pop());
        return builder.toString();
    }

    public static Long deCompress(String str) {
        Long result = 0L;
        Long multi = 1L;
        for (int i = str.length() - 1; i >= 0; --i) {
            result += symbolValues.get(str.charAt(i)) * multi;
            multi *= 60L;
        }
        return result;
    }

    // type  数字 10 是充值
    // 小写z
    // 60进制的 System.currentTimeMillis()
    // 小写z
    // 60进制的memberId
    // 小写z
    // payWay 1是微信 2是支付宝 小写a moneyType  充值金额 单位是分
    // 例如: 10 z 8aGcNeS z m z  eCLF
    public static String makeChargeUnifiedOrder(Integer memberId, PayWay payWay, Integer chargeMoney) {
        StringBuilder builder = new StringBuilder();
        long now = sysCurMil();
        builder.append("10");
        builder.append("z");
        builder.append(ImalCompress.compress(now));
        builder.append("z");
        builder.append(ImalCompress.compress(memberId));
        builder.append("z");
        builder.append(payWay.ordinal());
        builder.append("a");
        builder.append(ImalCompress.compress(chargeMoney));
        return builder.toString();
    }

    // type 11 支付宝认证
    // 小写z
    // 60进制的 System.currentTimeMillis()
    // 小写z
    // 60进制的memberId
    public static String makeAliCertificateOrder(Integer memberId) {
        StringBuilder builder = new StringBuilder();
        long now = sysCurMil();
        builder.append("11");
        builder.append("z");
        builder.append(ImalCompress.compress(now));
        builder.append("z");
        builder.append(ImalCompress.compress(memberId));
        return builder.toString();
    }

    public static Integer parseAliCertificateMemberId(String value) {
        if (StringUtils.isEmpty(value))
            return null;
        try {
            String[] zs = value.split("z");
            if (zs.length != 3)
                return null;
            return deCompressInt(zs[2]);
        } catch (Exception e) {
            return null;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChargeData {
        private PayWay payWay;
        private Integer canGetMoney;
        private Integer memberId;
        private Long createUnifiedOrderTime;
        private Integer chargeMoney;
    }

    public static ChargeData parseUnifiedOrder(String value) {
        if (StringUtils.isEmpty(value))
            return null;
        try {
            String[] zs = value.split("z");
            if (zs.length != 4)
                return null;

            long createUnifiedOrderTime = deCompress(zs[1]);
            int memberId = deCompressInt(zs[2]);
            // zs[2] == 1
            String[] as = zs[3].split("a");
            if (as.length != 4)
                return null;
            String payWayStr = as[0];
            int payWayInt;
            try {
                payWayInt = Integer.parseInt(payWayStr);
            } catch (Exception e) {
                return null;
            }
            PayWay payWay = PayWay.makePayWay(payWayInt);
            if (payWay == null)
                return null;
            Integer canGetMoney = deCompressInt(as[2]);
            Integer chargeMoney = deCompressInt(as[3]);
            return new ChargeData(payWay, canGetMoney, memberId, createUnifiedOrderTime, chargeMoney);
        } catch (Exception e) {
            return null;
        }
    }


}

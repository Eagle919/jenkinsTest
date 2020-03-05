package com.caih.cloud.iscs.common;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class AllInPayUtil {

    //private static final String secret = "6b929648fda44002ba68552c00628020";

    /**
     * 求一个字符串的md5值
     *
     * @param target 字符串
     * @return md5 value
     */
    public static String MD5(String target) {
        return DigestUtils.md5Hex(target);
    }

    /**
     * 参数加签
     *
     * @param treeMap
     * @return
     */
    public static String signData(Map<String, String> treeMap, String secret) {
        TreeMap<String, String> tMap = new TreeMap<>();
        for (Map.Entry<String, String> entry : treeMap.entrySet()) {
            if (!StringUtils.isEmpty(entry.getValue()) && !entry.getKey().equals("sign")) {
                tMap.put(entry.getKey(), entry.getValue());
            }
        }
        StringBuffer buf = new StringBuffer();
        for (String key : tMap.keySet()) {
            buf.append(key).append(treeMap.get(key));
        }
        buf.append(secret);
        return MD5(buf.toString()).toUpperCase();
    }

    /**
     * 验签
     *
     * @param treeMap
     * @return
     */
    public static boolean validsign(Map<String, String> treeMap, String secret) {
        String validsign = treeMap.get("sign");
        String sign = signData(treeMap, secret);
        if (!StringUtils.isEmpty(validsign) && validsign.equals(sign)) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {

        //提交商户信息接口加签
        Map<String, String> rs = new TreeMap<>();
        rs.put("storeNo", "2019102101");
        rs.put("storeName", "才华李直营店");
        rs.put("accNo", "chl");
        rs.put("userName", "小李");
        rs.put("phone", "13277883138");
        System.out.println(signData(rs, "64da38c9b0be44ee8c1606f9e4b6ab18"));











//        Map<String, String> rs = new TreeMap<>();
//        rs.put("storeNo", "201910151111000001");
//        rs.put("oldreqsn", "201910141111000111");
//        rs.put("trxamt", "0.01");
//        rs.put("reqsn", "2019101411110001119911");
//        System.out.println(signData(rs, "64da38c9b0be44ee8c1606f9e4b6ab18"));
//        Map<String, String> treeMap = new TreeMap<String, String>();
        // 支付加签
//        treeMap.put("cusid","990611048166001");
//        treeMap.put("appid","00009447");
//        treeMap.put("appkey","allinpay1234");
//        treeMap.put("trxamt","1");
//        treeMap.put("reqsn","201908204501000001");
//        treeMap.put("charset","UTF-8");
//        treeMap.put("returl","ttp://222.84.157.37:22001/iscs-code/user-pay/index.html");
//        treeMap.put("notify_url","http://222.84.157.37:22001/iscs/pay/payNotify");
//        treeMap.put("body","payDetail");
//        treeMap.put("remark","buyer:张三");
//        String secret = "6b929648fda44002ba68552c00628020";

        //退款加签
//        treeMap.put("cusid","990611048166001");
//        treeMap.put("appid","00009447");
////        treeMap.put("trxamt","1");
////        treeMap.put("reqsn","201908204501000001");
//        treeMap.put("oldxtyjorderno","20190820450100000199");
//        String secret = "6b929648fda44002ba68552c00628020";

//        //验签
//        treeMap.put("orderNo","201908204501000001");
//        treeMap.put("storeNo","201908121230000001");
//        treeMap.put("buyer","buyer:张三");
//        String secret = "64da38c9b0be44ee8c1606f9e4b6ab18";//"6b929648fda44002ba68552c00628020";

//        System.out.println(signData(treeMap, secret));

//        Map treeMap1 = new TreeMap();
//        treeMap1.put("xtyjorderno","1234567890");
//        treeMap1.put("reqsn","201807120064350231");
//        treeMap1.put("trxstatus","0000");
//        treeMap1.put("trxid","100010001");
//        treeMap1.put("cusid","11111");
//        String secret1 = "6b929648fda44002ba68552c00628020";//通联
        //http://xtest.allinpaygx.com/wx/cash/externalorder/unionorder?cusid=990611048166001&appid=00009447&appkey=allinpay1234&trxamt=1&reqsn=2019080800001&charset=UTF-8&returl=http://www.baidu.com&notify_url=http://xtest.allinpaygx.com/wx/cash/notifytest&body=orderTitleName &remark=orderRemark&sign=DA592D21EB41D903F93B810DD9578820
        //退款加签
//        System.out.println(signData(treeMap1, secret1));


    }

}


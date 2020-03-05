package com.caih.cloud.iscs.charge.constants;

public class WechatPayConstants {

    public static final String SUCCESS = "success";

    public static final String FAIL = "fail";

    //文件分隔符
    public static final String SF_FILE_SEPARATOR = System.getProperty("file.separator");

    // 微信退款接口(POST)
    public final static String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    // 微信支付统一接口(POST)
    public final static String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    // 微信查询订单接口(POST)
    public final static String ORDER__QUERY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";

    // 微信支付失败跳转地址
    public final static String PAY_FAIL_URL = "http://ebike.housent.cn/charge/#/charging-pile/failure";
    public static final String CERT_PATH = "apiclient_cert.p12" ;
}

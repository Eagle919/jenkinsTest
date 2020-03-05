package com.caih.cloud.iscs.charge.service.impl;


import com.caih.cloud.iscs.charge.aasecretkey.MyWXPayConfig;
import com.caih.cloud.iscs.charge.constants.AliPayConstants;
import com.caih.cloud.iscs.charge.constants.WechatPayConstants;
import com.caih.cloud.iscs.charge.model.entity.EOrder;
import com.caih.cloud.iscs.charge.service.EOrderService;
import com.caih.cloud.iscs.charge.service.ESocketService;
import com.caih.cloud.iscs.charge.service.WechatPayService;
import com.caih.cloud.iscs.charge.utils.EWechatPayUtils;
import com.caih.cloud.iscs.charge.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

@Slf4j
@Service
public class WechatPayServiceImpl implements WechatPayService {

    @Resource
    private MyWXPayConfig myWXPayConfig;

    @Resource
    private EOrderService orderService;

    @Resource
    private ESocketService socketService;


    @Override
    public Boolean refund(String outTradeNo, String totalFee) {

        log.info("订单号：{}微信退款", outTradeNo);

        boolean result = true;

        try {
            // 账号信息
            String mch_id = myWXPayConfig.getMch_id(); // 商业号
            String key = myWXPayConfig.getKey(); // key
            String appid = myWXPayConfig.getAppid(); // appid

            SortedMap<Object, Object> packageParams = packBaseArgs(mch_id, appid);

            packageParams.put("out_refund_no", UUID.randomUUID().toString().replace("-", "").toUpperCase());// 商户订单号
            packageParams.put("out_trade_no", outTradeNo);// 商户订单号
            BigDecimal returnFee = new BigDecimal(totalFee).multiply(new BigDecimal(100));
            String fee = returnFee + "";
            String money = EWechatPayUtils.subZeroAndDot(fee);
            packageParams.put("refund_fee", money);//退款金额
            packageParams.put("total_fee", money);// 总金额

            //todo 退款成功后更新订单信息

            String sign = EWechatPayUtils.createSign("UTF-8", packageParams, key);
            packageParams.put("sign", sign);// 签名

            String requestXML = EWechatPayUtils.getRequestXml(packageParams);

            System.out.println("requestXML = " + requestXML);

            String weixinPost = EWechatPayUtils.WechatRefund(WechatPayConstants.REFUND_URL,
                    requestXML, myWXPayConfig.getMch_id());
            Map map = EWechatPayUtils.doXMLParse(weixinPost);
            System.out.println("weixinPost = " + map);

            assert map != null;
            String returnCode = (String) map.get("return_code");
            if ("SUCCESS".equals(returnCode)) {
                String resultCode = (String) map.get("result_code");
                if ("SUCCESS".equals(resultCode)) {
                    log.info("订单号：{}微信退款成功", outTradeNo);
                } else {
                    String errCodeDes = (String) map.get("err_code_des");
                    log.info("订单号：{}微信退款失败:{}", outTradeNo, errCodeDes);
                    result = false;
                }
            } else {
                String returnMsg = (String) map.get("return_msg");
                log.info("订单号：{}微信退款失败:{}", outTradeNo, returnMsg);
                result = false;
            }
        } catch (Exception e) {
            log.error("订单号：{}微信支付失败(系统异常)", outTradeNo, e);
            result = false;
        }
        return result;

    }

    private SortedMap<Object, Object> packBaseArgs(String mch_id, String appid) {
        //基础参数
        SortedMap<Object, Object> packageParams = new TreeMap<>();
        // 生成随机字符串
        String nonce_str = getString();
        packageParams.put("appid", appid);// 公众账号ID
        packageParams.put("mch_id", mch_id);// 商户号
        packageParams.put("nonce_str", nonce_str);// 随机字符串
        return packageParams;
    }

    @Override
    public String h5pay(EOrder order, String desc, int money, String spbillCreateIp) {
        log.info("订单号：{}发起H5支付", order.getOrderNo());
        String mweb_url = null;
        try {
            // 账号信息
            String key = myWXPayConfig.getKey(); // key
            String trade_type = "MWEB";//交易类型 H5 支付
            //封装基础参数
            SortedMap<Object, Object> packageParams = new TreeMap<>();
            packageParams.put("body", desc);// 商品描述
            packageParams.put("out_trade_no", order.getOrderNo());// 商户订单号
//            packageParams.put("out_trade_no", order.getOrderNo());// 部分退款订单号
            String totalFee = money + "";
            packageParams.put("total_fee", totalFee);// 总金额

            //H5支付要求商户在统一下单接口中上传用户真实ip地址 spbill_create_ip

            packageParams.put("spbill_create_ip", spbillCreateIp);// 发起人IP地址
            packageParams.put("notify_url", myWXPayConfig.getCallbackUrl());// 回调地址
            packageParams.put("trade_type", trade_type);// 交易类型

            packageParams.put("appid", myWXPayConfig.getAppid());
            packageParams.put("mch_id", myWXPayConfig.getMch_id());

            // 生成随机字符串
            packageParams.put("nonce_str", getString());


            //H5支付专用
            JSONObject value = new JSONObject();
            value.put("type", "WAP");
            value.put("wap_url", "https://ebike.housent.cn/");////WAP网站URL地址
            value.put("wap_name", "智慧社区充电服务充值中心");//WAP 网站名
            JSONObject scene_info = new JSONObject();
            scene_info.put("h5_info", value);
            packageParams.put("scene_info", scene_info.toString());

            String sign = EWechatPayUtils.createSign("UTF-8", packageParams, key);
            packageParams.put("sign", sign);// 签名

            String requestXML = EWechatPayUtils.getRequestXml(packageParams);
            String resXml = EWechatPayUtils.postData(WechatPayConstants.UNIFIED_ORDER_URL, requestXML);
            Map map = EWechatPayUtils.doXMLParse(resXml);
            assert map != null;
            String returnCode = (String) map.get("return_code");
            if ("SUCCESS".equals(returnCode)) {
                String resultCode = (String) map.get("result_code");
                if ("SUCCESS".equals(resultCode)) {
                    log.info("订单号：{}发起H5支付成功", order.getOrderNo());
                    mweb_url = (String) map.get("mweb_url");
                    //确认支付过后跳的地址,需要经过urlencode处理
                    String urlString = URLEncoder.encode(AliPayConstants.return_url, "GBK");
                    mweb_url += "&redirect_url=" + urlString;

                } else {
                    String errCodeDes = (String) map.get("err_code_des");
                    log.info("订单号：{}发起H5支付(系统)失败:{}", order.getOrderNo(), errCodeDes);
                    //支付失败 跳转到支付失败页面
                    mweb_url = WechatPayConstants.PAY_FAIL_URL;
                    //update order
                    order.setPayStatus(3); //(系统)失败
                    order.setStatus(3);
                    order.setUpdateTime(new Date());
                    orderService.updateById(order);

                }
            } else {
                String returnMsg = (String) map.get("return_msg");
                log.info("(订单号：{}发起H5支付(通信)失败:{}", order.getOrderNo(), returnMsg);
                //update order
                order.setPayStatus(4); //(通信)失败
                order.setStatus(3);
                orderService.updateById(order);
                //支付失败 跳转到支付失败页面
                mweb_url = WechatPayConstants.PAY_FAIL_URL;
            }
        } catch (Exception e) {
            //支付失败 跳转到支付失败页面
            mweb_url = WechatPayConstants.PAY_FAIL_URL;
            log.error("订单号：{}发起H5支付失败(系统异常))", order.getOrderNo(), e);
        }
        return mweb_url;
    }

    /**
     * SUCCESS—支付成功
     * REFUND—转入退款
     * NOTPAY—未支付
     * CLOSED—已关闭
     * REVOKED—已撤销（刷卡支付）
     * USERPAYING--用户支付中
     * PAYERROR--支付失败(其他原因，如银行返回失败)
     * 支付状态机请见下单API页面
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Boolean orderquery(String orderNo) {

        boolean result;

        try {
            // 账号信息
            String key = myWXPayConfig.getKey(); // key
            TreeMap<Object, Object> packageParams = new TreeMap<>();
            // 账号信息
            String appid = myWXPayConfig.getAppid(); // appid
            String mch_id = myWXPayConfig.getMch_id(); // 商业号

            packageParams.put("appid", appid);// 公众账号ID
            packageParams.put("mch_id", mch_id);// 商户号
            packageParams.put("nonce_str", getString());// 随机字符串


            packageParams.put("out_trade_no", orderNo);// 商户订单号
            String sign = EWechatPayUtils.createSign("UTF-8", packageParams, key);
            packageParams.put("sign", sign);// 签名
            log.info("订单查询参数【" + packageParams + "】");

            String requestXML = EWechatPayUtils.getRequestXml(packageParams);
            String resXml = EWechatPayUtils.postData(WechatPayConstants.ORDER__QUERY_URL, requestXML);
            Map map = EWechatPayUtils.doXMLParse(resXml);
            assert map != null;
            String returnCode = (String) map.get("return_code");
            log.info(returnCode);
            if ("SUCCESS".equals(returnCode)) {
                String resultCode = (String) map.get("result_code");
                if ("SUCCESS".equals(resultCode)) {
                    String tradeState = (String) map.get("trade_state");
                    log.info(tradeState);
                    result = true;
                } else {
                    String errCodeDes = (String) map.get("err_code_des");
                    log.info(errCodeDes);
                    result = false;
                }
            } else {
                String returnMsg = (String) map.get("return_msg");
                log.info(returnMsg);
                result = false;
            }
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }


    private String getString() {
        // 生成随机字符串
        String currTime = TimeUtils.getCurrTime();
        String strTime = currTime.substring(8);
        String strRandom = EWechatPayUtils.buildRandom(4) + "";
        return strTime + strRandom;
    }
}

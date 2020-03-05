package com.caih.cloud.iscs.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("交易退款响应参数")
public class RefundRespondSuccess {
    private String retcode;//返回码（SUCCESS/FAIL）
    private String retmsg;//返回码说明
    //    private String cusid;//商户号
//    private String appid;//应用ID
    private String trxid;//交易单号
    private String reqsn;//商户订单号
    //trxstatus 交易状态
    //0000-交易成功 1001-交易不存在 3888-流水号重复        3889-交易控制失败，具体原因看errmsg   3099-渠道商户错误
    //3014-交易金额小于应收手续费   3031-校验实名信息失败  3088-交易未支付(在查询时间区间内未成功支付,如已影响资金24小时内会做差错退款处理)
    //3089-撤销异常,如已影响资金24小时内会做差错退款处理   3045-其他错误，具体原因看errmsg       3999-其他错误，具体原因看errmsg
    //其他3开头的错误码代表交易失败,具体原因请读取errmsg
    private String trxstatus;
    private String fintime;//交易完成时间（yyyyMMddHHmmss）
    private String errmsg;//错误原因
    private String fee;//手续费
    //trxcode 交易类型
    //VSP501:微信支付      VSP502：微信支付撤销 VSP503：微信支付退款    VSP505: 手机QQ支付      VSP506: 手机QQ支付撤销
    //SP507:手机QQ支付退款 VSP511 : 支付宝支付  VSP512 : 支付宝支付撤销 VSP513 : 支付宝支付退款 VSP551 : 银联扫码支付
    //VSP552 :银联扫码撤销 VSP553 : 银联扫码退货
    private String trxcode;
//    private String randomstr;//随机字符串
//    private String sign;//签名
}

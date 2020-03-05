package com.caih.cloud.iscs.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单支付二维码临时表
 */
@Data
@TableName("temp_order_qrcode")
@ApiModel(value = "qrcode", description = "订单支付二维码")
public class TempOrderQrcode extends Model<TempOrderQrcode> {
    @ApiModelProperty("支付验证码")
    private String validKey;
    @ApiModelProperty("订单编号")
    private String orderNo;
    @ApiModelProperty("二维码")
    private byte[] qrCode;
    @ApiModelProperty("回调地址")
    private String retUrl;

    //交易结果通知数据
    @ApiModelProperty("通联分配的appid")
    private String appid;
    @ApiModelProperty("第三方交易号")
    private String outtrxid;
    @ApiModelProperty("交易类型")
    private String trxcode;
    @ApiModelProperty("通联收银宝交易流水号")
    private String trxid;
    @ApiModelProperty("金额，单位：分")
    private String trxamt;
    @ApiModelProperty("交易请求日期，格式：yyyymmdd")
    private String trxdate;
    @ApiModelProperty("交易完成时间，格式：yyyyMMddHHmmss")
    private String paytime;
    @ApiModelProperty("渠道流水号，如支付宝,微信平台订单号")
    private String chnltrxid;
    @ApiModelProperty("交易状态")
    private String trxstatus;
    @ApiModelProperty("收银宝商户号")
    private String cusid;
    @ApiModelProperty("终端编码")
    private String termno;
    @ApiModelProperty("终端批次号")
    private String termbatchid;
    @ApiModelProperty("终端流水")
    private String termtraceno;
    @ApiModelProperty("终端授权码")
    private String termauthno;
    @ApiModelProperty("终端参考号")
    private String termrefnum;
    @ApiModelProperty("业务关联内容:交易备注")
    private String trxreserved;
    @ApiModelProperty("通联原交易流水,冲正撤销交易本字段不为空")
    private String srctrxid;
    @ApiModelProperty("业务流水,如订单号，保单号，缴费编号等")
    private String cusorderid;
    @ApiModelProperty("交易帐号")
    private String acct;
    @ApiModelProperty("手续费，单位：分")
    private String fee;
    @ApiModelProperty("签名类型,MD5或RSA。为空默认MD5")
    private String signtype;
    @ApiModelProperty("sign校验码,用商户的md5key进行校验")
    private String sign;
}

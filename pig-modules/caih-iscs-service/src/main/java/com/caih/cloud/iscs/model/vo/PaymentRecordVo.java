package com.caih.cloud.iscs.model.vo;

import com.caih.cloud.iscs.model.base.BaseEntity;
import com.caih.cloud.iscs.model.entity.TempOrderQrcode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.pig.common.util.serializer.DateFormatYmtHmsSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Kang
 * @date 2019/10/12
 */
@Data
@ApiModel("支付交易信息")
public class PaymentRecordVo extends BaseEntity {
    @ApiModelProperty("商户编号")
    private String storeNo;
    @ApiModelProperty("交易金额,单位为分")
    private String trxamt;
    @ApiModelProperty("手续费,单位为分")
    private String fee;
    @ApiModelProperty("交易完成时间:yyyyMMdd HHmmss")
    @JsonSerialize(using = DateFormatYmtHmsSerializer.class)
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date paytime;
    @ApiModelProperty("交易状态")
    private String trxstatus;
    @ApiModelProperty("商户的交易订单号")
    private String cusorderid;

    /*@ApiModelProperty("平台分配的商户号")
    private String cusid;
    @ApiModelProperty("平台分配的APPID")
    private String appid;
    @ApiModelProperty("平台的交易流水号")
    private String trxid;
    @ApiModelProperty("支付渠道交易单号,如支付宝,微信平台的交易单号")
    private String chnltrxid;
    @ApiModelProperty("商户的交易订单号")
    private String reqsn;
    @ApiModelProperty("交易类型")
    private String trxcode;
    @ApiModelProperty("交易金额,单位为分")
    private String trxamt;
    @ApiModelProperty("交易状态")
    private String trxstatus;
    @ApiModelProperty("支付平台用户标识")
    private String acct;
    @ApiModelProperty("交易完成时间:yyyyMMddHHmmss")
    private String fintime;
    @ApiModelProperty("手续费,单位为分")
    private String fee;
    @ApiModelProperty("随机生成的字符串")
    private String randomstr;
    @ApiModelProperty("错误原因")
    private String errmsg;
    @ApiModelProperty("签名")
    private String sign;*/
}

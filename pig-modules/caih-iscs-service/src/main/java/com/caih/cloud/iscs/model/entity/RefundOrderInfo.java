package com.caih.cloud.iscs.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.pig.common.util.serializer.DateFormatYmtHmsSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("iscs_refund_order_info")
@ApiModel(value = "payOrder", description = "订单退款信息表")
public class RefundOrderInfo extends Model<RefundOrderInfo> {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 退款订单编号
     */
    private String refundOrderNo;

    /**
     * 原订单编号
     */
    private String orderNo;

    /**
     * 交易状态-通联返回
     */
    private String trxstatus;

    /**
     * 失败的原因说明
     */
    private String errmsg;

    /**
     * 手续费,单位：分
     */
    private BigDecimal fee;

    /**
     * 交易类型
     */
    private String trxcode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 退款申请时间
     */
    @JsonSerialize(using = DateFormatYmtHmsSerializer.class)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String refundTime;

    /**
     * 交易完成时间:yyyyMMddHHmmss
     */
    @JsonSerialize(using = DateFormatYmtHmsSerializer.class)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String fintime;
}

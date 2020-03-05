package com.caih.cloud.iscs.charge.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.caih.cloud.iscs.model.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 充电桩-订单表
 */
@Data
@TableName("e_order")
public class EOrder extends BaseEntity {

    /**
     * 设备ID
     */
    private Integer deviceId; //

    /**
     * 小区ID
     */
    private Integer communityId; //

    /**
     * 用户ID
     */
    private Integer memberId; //

    /**
     * 插座ID
     */
    private Integer socketId; //

    /**
     * 编号
     */
    private String orderNo; //

    /**
     * 订单状态：0|订单取消 1|正在充电 2|充电完成 3|支付失败
     */
    private Integer status; ///

    /**
     * 支付金额：单位 元
     */
    private BigDecimal pay; //

    /**
     * 支付方式 1|支付宝支付  2|微信支付
     */
    private Integer payWay; //

    /**
     * 付款状态：1|待付款 2|已付款 3|系统(失败) 4|(通信)失败 5|订单完成
     */
    private Integer payStatus; ///

    /**
     * 退款金额：单位 元
     */
    private BigDecimal refund;

    /**
     * 充电时间：单位 小时
     */
    private String chargeTime;

}

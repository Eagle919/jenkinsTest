package com.caih.cloud.iscs.charge.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class EOrderVo {
    private Integer orderStatus;
    private String orderNo;
    private String deviceNO;
    private String communityName;
    private String memberName;
    private String chargeTime;
    private String pay;
    private String actualPay;
    private String refund;
    // 1 有退款  2 无退款
    private Integer refundStatus;
    private Date createTime;
}

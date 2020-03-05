package com.caih.cloud.iscs.charge.model.vo;

import lombok.Data;

@Data
public class EPayVo {
    private Integer orderStatus;
    private String orderNo;
    private String deviceNO;
    private String communityName;
    private String memberName;
    private String chargeTime;
    private String pay;
    private String actualPay;
    private String refund;
}

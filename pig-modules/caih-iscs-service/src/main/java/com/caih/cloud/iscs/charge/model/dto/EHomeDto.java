package com.caih.cloud.iscs.charge.model.dto;

import com.caih.cloud.iscs.model.base.BaseDto;
import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class EHomeDto extends BaseDto {

    private String deviceNo;

//    private Integer userId;

    private String accessToken;

    private String userName;

    private String floor;

    private String hour;

    private Integer socketId;

    private Integer orderId;

    private String orderNo;//test

    //支付方式 1|支付宝支付  2|微信支付
    private Integer payWay;


}

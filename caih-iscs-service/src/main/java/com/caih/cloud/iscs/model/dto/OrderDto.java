package com.caih.cloud.iscs.model.dto;

import com.caih.cloud.iscs.model.base.BaseQo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderDto extends BaseQo {
    @ApiModelProperty("订单开始时间")
    private String startTime;
    @ApiModelProperty("订单截止时间")
    private String endTime;
    @ApiModelProperty("订单编号")
    private String orderNo;
    @ApiModelProperty("买家姓名")
    private String buyerName;
    @ApiModelProperty("买家电话")
    private String buyerPhone;
    @ApiModelProperty("订单状态")
    private Integer orderStatus;
    @ApiModelProperty("用户id")
    private Integer userId;
    @ApiModelProperty("商户编号")
    private String StoreNo;
}

package com.caih.cloud.iscs.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.pig.common.util.serializer.DateFormatYmtHmsSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel("订单展示信息")
public class OrderVo {
    @ApiModelProperty("订单id")
    private Integer id;
    @ApiModelProperty("订单总金额")
    private String orderAmt;
    @ApiModelProperty("订单编号")
    private String orderNo;
    @ApiModelProperty("商家编号")
    private String storeNo;
    @ApiModelProperty("交易号")
    private String dealNo;
    @JsonSerialize(using = DateFormatYmtHmsSerializer.class)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("下单时间")
    private Date orderTime;
    @ApiModelProperty("买家付款时间")
    @JsonSerialize(using = DateFormatYmtHmsSerializer.class)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;
    @ApiModelProperty("发货时间")
    @JsonSerialize(using = DateFormatYmtHmsSerializer.class)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deliveryTime;
    @ApiModelProperty("完成时间")
    @JsonSerialize(using = DateFormatYmtHmsSerializer.class)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;
    @ApiModelProperty("商品集合")
    private List<GoodInfoVo> goods;
    @ApiModelProperty("买家姓名")
    private String buyerName;
    @ApiModelProperty("买家电话")
    private String buyerPhone;
    @ApiModelProperty("买家备注")
    private String buyerRemarks;
    @ApiModelProperty("收货地址")
    private String buyerAddress;
    @ApiModelProperty("邮费")
    private String postage;
    @ApiModelProperty("状态数字")
    private Integer status;
    @ApiModelProperty("状态中文")
    private String statusCN;
    @ApiModelProperty("物流公司编号")
    private String expressComNo;
    @ApiModelProperty("物流公司名称")
    private String expressComName;
    @ApiModelProperty("物流单号")
    private String expressNo;
}

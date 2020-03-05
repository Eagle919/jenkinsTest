package com.caih.cloud.iscs.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.pig.common.util.serializer.DateFormatYmtHmsSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("iscs_order_info")
@ApiModel(value = "payOrder", description = "订单详情表")
public class OrderInfo1 extends Model<OrderInfo> {
    @ApiModelProperty("ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty("订单编号")
    private String orderNo;
    @ApiModelProperty("订单时间")
    @JsonSerialize(using = DateFormatYmtHmsSerializer.class)
    /*@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")*/
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date orderTime;
    @ApiModelProperty("订单状态")
    private String orderState;
    @ApiModelProperty("商户编号")
    private String storeNo;
    @ApiModelProperty("邮费")
    private BigDecimal postage;
    @ApiModelProperty("买家")
    private String buyer;
    @ApiModelProperty("买家电话")
    private String buyerPhone;
    @ApiModelProperty("收货地址")
    private String buyerAddress;
    @ApiModelProperty("买家备注")
    private String buyerRemarks;
    @ApiModelProperty("付款时间")
    private Date payTime;
    @ApiModelProperty("交易单号")
    private String dealNo;
    @ApiModelProperty("快递公司编号")
    private String expressComNo;
    @ApiModelProperty("快递单号")
    private String expressNo;
    @ApiModelProperty("发货时间")
    private Date deliveryTime;
}

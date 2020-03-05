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

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("iscs_order_info")
@ApiModel(value = "payOrder", description = "订单详情表")
public class OrderInfo extends Model<OrderInfo> {
    @ApiModelProperty("ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @NotNull(message = "订单编号不能为空")
    @Size(min=1, max=36, message = "订单编号最长不超过36字符")
    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("订单时间")
    @JsonSerialize(using = DateFormatYmtHmsSerializer.class)
    /*@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")*/
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date orderTime;

    @ApiModelProperty("订单状态")
    private Integer orderState;

    @NotNull(message = "商户编号不能为空")
    @Size(min=1, max=36, message = "商户编号最长不超过36字符")
    @ApiModelProperty("商户编号")
    private String storeNo;

    @DecimalMin(value = "0.00", message = "最小值为0.00")
    @Digits(integer = 8, fraction = 2, message = "邮费整数不能超过8位且保留2位小数")
    @ApiModelProperty("邮费")
    private BigDecimal postage;

    @NotNull(message = "买家不能为空")
    @Size(min=1, max=50, message = "买家最长不超过50字符")
    @ApiModelProperty("买家")
    private String buyer;

    @NotNull(message = "买家电话不能为空")
    //^[1](([3|5|8][\\d])|([4][4,5,6,7,8,9])|([6][2,5,6,7])|([7][^9])|([9][1,8,9]))[\\d]{8}$
    @Pattern(regexp = "^[1][3,4,5,7,8][0-9]{9}$", message = "填写正确的手机号码")
    @ApiModelProperty("买家电话")
    private String buyerPhone;

    @NotNull(message = "收货地址不能为空")
    @Size(min=1, max=200, message = "收货地址最长不超过200字符")
    @ApiModelProperty("收货地址")
    private String buyerAddress;

    @Size(min=0, max=500, message = "买家备注最长不超过500字符")
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

    @ApiModelProperty("分账信息")
    private String asinfo;

}

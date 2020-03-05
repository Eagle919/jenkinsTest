package com.caih.cloud.iscs.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 订单商品信息表
 */
@Data
@TableName("iscs_order_goods_info")
@ApiModel(value = "orderGoods", description = "订单商品信息表")
public class OrderGoodsInfo extends Model<OrderGoodsInfo> {
    @ApiModelProperty("ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Size(min=1, max=36, message = "订单编号最长不超过36字符")
    @ApiModelProperty("订单编号")
    private String orderNo;

    @NotNull(message = "商品编号不能为空")
    @Size(min=1, max=36, message = "商品编号最长不超过36字符")
    @ApiModelProperty("商品编号")
    private String goodsNo;

    @NotNull(message = "商品名称不能为空")
    @Size(min=1, max=50, message = "商品名称最长不超过50字符")
    @ApiModelProperty("商品名称")
    private String goodsName;

    //Digits的fraction验证不了Integer
    @NotNull(message = "商品数量不能为空")
    @DecimalMin(value = "0", message = "最小值为0")
    @Digits(integer = 8, fraction = 0, message = "商品数量为正整数且不能超过8位")
    @ApiModelProperty("商品数量")
    private BigDecimal goodsNum;

    @NotNull(message = "商品单价不能为空")
    @DecimalMin(value = "0.00", message = "最小值为0.00")
    @Digits(integer = 8, fraction = 2, message = "商品单价整数不能超过8位且保留2位小数")
    @ApiModelProperty("商品单价")
    private BigDecimal goodsPrice;
}

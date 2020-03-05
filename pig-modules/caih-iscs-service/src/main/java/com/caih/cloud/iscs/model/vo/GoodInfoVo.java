package com.caih.cloud.iscs.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodInfoVo {
    @ApiModelProperty("商品编号")
    private String goodsNo;
    @ApiModelProperty("商品名称")
    private String goodsName;
    @ApiModelProperty("商品数量")
    private Integer goodsNum;
    @ApiModelProperty("商品单价")
    private String goodsPrice;
}

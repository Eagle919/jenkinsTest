package com.caih.cloud.iscs.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengheng
 * @date 2019/08/05
 */
@Data
@ApiModel("商户展示信息")
public class ScTurnoverVo {
    @ApiModelProperty("序号")
    private Integer id;
    @ApiModelProperty("交易单号")
    private String dealNo;
    @ApiModelProperty("商户名称")
    private String storeName;
    @ApiModelProperty("流水类型")
    private String turnoverType;
    @ApiModelProperty("流水金额")
    private String turnoverAmt;
    @ApiModelProperty("账户余额")
    private String accBalance;
    @ApiModelProperty("备注")
    private String remarks;
    @ApiModelProperty("流水记录时间")
    private String turnoverDate;
}

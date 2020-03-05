package com.caih.cloud.iscs.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.pig.common.util.serializer.DateFormatYmtHmsSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ApiModel("提现记录展示信息")
public class ExtractVo {
    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("提现时间")
    @JsonSerialize(using = DateFormatYmtHmsSerializer.class)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date extractTime;

    @ApiModelProperty("提现单号|处理单号")
    private String dealNo;

    @ApiModelProperty("提现金额")
    private String extractAmt;

    @ApiModelProperty("提现编号")
    private String extractNo;

    @ApiModelProperty("账号")
    private String extractAcc;

    @ApiModelProperty("商户编号")
    private String storeNo;

    @NotNull(message = "账号编号不能为空")
    @ApiModelProperty("账号编号")
    private String accNo;

    @ApiModelProperty("开户银行")
    private String bank;

    @ApiModelProperty("开户支行")
    private String subBranch;

    @ApiModelProperty("开户人")
    private String opener;

    @ApiModelProperty("状态")
    private Integer extractState;

    @ApiModelProperty("状态名称")
    private String extractStateName;

    @ApiModelProperty("备注")
    private String remarks;
}

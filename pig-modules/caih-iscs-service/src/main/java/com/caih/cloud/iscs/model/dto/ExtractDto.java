package com.caih.cloud.iscs.model.dto;

import com.caih.cloud.iscs.model.base.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel("提现记录前端过滤条件")
public class ExtractDto extends BaseDto {
    @NotNull(message = "用户id不能为空")
    @ApiModelProperty("用户id")
    private Integer userId = null;
    @ApiModelProperty("开始时间")
    private String startTime = null;
    @ApiModelProperty("结束时间")
    private String endTime = null;
    @ApiModelProperty("开户人")
    private String opener = null;
    ////提现审核参数 搜索参数
    @NotNull(message = "审核状态不能为空")
    @ApiModelProperty("提现状态:0-待审核 1-已拒绝 2-已通过未转账 3-已转账")
    private Integer extractState = null;
    //提现申请参数
    @ApiModelProperty("提现金额")
    private BigDecimal extractAmt = null;

    //提现审核参数
    @ApiModelProperty("提现编号")
    @NotNull(message = "提现编号不能为空")
    private String extractNo = null;
    @NotNull(message = "拒绝理由不能为空")
    @ApiModelProperty("拒绝理由")
    private String reason = null;

}

package com.caih.cloud.iscs.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ExpressInfoVo {
    @ApiModelProperty("物流公司")
    private String expressComName;
}

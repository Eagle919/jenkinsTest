package com.caih.cloud.iscs.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class BaseDto extends BaseEntity {
    //当前页码
    @ApiModelProperty("页码")
    private int page = 1;
    //每页条数
    @ApiModelProperty("每页条数")
    private int limit = 10;
}

package com.caih.cloud.iscs.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yuwei
 * @date 2018/11/13 14:55
 */
@Data
public class BaseQo extends BaseEntity {
    //当前页码
    @ApiModelProperty("页码")
    private int page = 1;
    //每页条数
    @ApiModelProperty("每页条数")
    private int limit = 10;
}

package com.caih.cloud.iscs.charge.model.dto;

import com.caih.cloud.iscs.model.base.BaseDto;
import lombok.Data;

@Data
public class EOrderDto extends BaseDto {

    private Integer userId;

    private String orderNo;

    private Integer status;
}

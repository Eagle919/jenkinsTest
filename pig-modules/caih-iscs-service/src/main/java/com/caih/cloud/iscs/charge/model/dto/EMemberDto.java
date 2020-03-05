package com.caih.cloud.iscs.charge.model.dto;

import com.caih.cloud.iscs.model.base.BaseDto;
import lombok.Data;

@Data
public class EMemberDto extends BaseDto {

    private Integer userId;

    private String name;
    private String mobileTel;
    private String tel;
    private String email;
}

package com.caih.cloud.iscs.charge.model.dto;

import com.caih.cloud.iscs.model.base.BaseDto;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ECommunityDto extends BaseDto {

    private Integer communityId;

    private Integer userId;

    private String name;

    private String address;

    private String rate;

    private String remark;

    //社区新增相关字段

}

package com.caih.cloud.iscs.charge.model.dto;

import com.caih.cloud.iscs.model.base.BaseDto;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class EDeviceDto extends BaseDto {

    private Integer userId;

    //设备状态：1 在线，2 离线
    private Integer online;

    private String deviceNo;

    private String deviceId;

    private String communityName;
    @NotBlank(message = "小区ID不能为空")
    private Integer communityId;
}

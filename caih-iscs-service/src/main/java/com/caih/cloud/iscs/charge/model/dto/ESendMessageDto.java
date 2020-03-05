package com.caih.cloud.iscs.charge.model.dto;

import com.caih.cloud.iscs.model.base.BaseDto;
import lombok.Data;

@Data
public class ESendMessageDto extends BaseDto {
    /**
     * 端口号
     */
    private Integer port;

    /**
     * 设备号
     */

    private String deviceCode;

    /**
     * 时长
     */
    private Integer timer;
}

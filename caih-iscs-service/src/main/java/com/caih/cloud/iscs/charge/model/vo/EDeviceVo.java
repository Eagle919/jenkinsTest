package com.caih.cloud.iscs.charge.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EDeviceVo {
    private Integer id;
    private Integer communityId;
    private String deviceNo;
    private String communityName;
    private BigDecimal communityRate;
    private Integer online;
    private Integer totalSocketNumber;
    private Integer freeSocketNumber;
}

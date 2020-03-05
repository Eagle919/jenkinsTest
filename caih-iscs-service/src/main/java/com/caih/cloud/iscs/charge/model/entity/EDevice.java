package com.caih.cloud.iscs.charge.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.caih.cloud.iscs.model.base.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 充电桩-设备表
 */
@Data
@TableName("e_device")
public class EDevice extends BaseEntity {

    /**
     * 小区id
     */
    private Integer communityId;

    /**
     * 设备编号
     */
    private String deviceNo;

    /**
     * 设备状态：1 在线，2 离线
     */
    private Integer online;

}

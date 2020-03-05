package com.caih.cloud.iscs.charge.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.caih.cloud.iscs.model.base.BaseEntity;
import lombok.Data;

/**
 * 充电桩-小区楼栋表
 */
@Data
@TableName("e_building")
public class EBuilding extends BaseEntity {
    /**
     * 楼栋编号
     */
    private String buildingNo;

    /**
     * 楼栋名称
     */
    private String name;
}

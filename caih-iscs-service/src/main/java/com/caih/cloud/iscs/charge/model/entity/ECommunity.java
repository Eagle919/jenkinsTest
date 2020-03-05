package com.caih.cloud.iscs.charge.model.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.caih.cloud.iscs.model.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 充电桩-小区表
 */
@Data
@TableName("e_community")
public class ECommunity extends BaseEntity {

    /**
     * 小区编号
     */
    private String communityNo;

    /**
     * 小区名称
     */
    private String name;

    /**
     * 小区地址
     */
    private String address;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户编号 n
     */
    private String userNo;

    /**
     * 用户楼栋
     */
    private String floor;

    /**
     * 用户楼栋编号 n
     */
    private String buildingNo;

    /**
     * 收费标准：单位->元/小时
     */
    private String rate;

    /**
     * 收费备注
     */
    private String remark;

}

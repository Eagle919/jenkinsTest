package com.caih.cloud.iscs.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caih.cloud.iscs.model.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 账户信息表
 */
@Data
@TableName("acc_info")
public class AccInfo extends BaseEntity {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 账户编号
     */
    private String accNo;

    /**
     * 商户编号
     */
    private String storyNo;

    /**
     * 提现账户
     */
    private String extractAcc;

    /**
     * 开户银行
     */
    private String bank;

    /**
     * 开户人
     */
    private String opener;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 修改时间
     */
    private Date updateDate;
}

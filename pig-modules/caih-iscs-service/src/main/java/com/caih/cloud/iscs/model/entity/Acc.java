package com.caih.cloud.iscs.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caih.cloud.iscs.model.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.pig.common.util.serializer.DateFormatYmtHmsSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 账户信息表
 */
@Data
@TableName("iscs_acc_info")
public class Acc extends BaseEntity {
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
    private String storeNo;

    /**
     * 提现账户
     */
    private String extractAcc;

    /**
     * 开户银行
     */
    private String bank;

    /**
     * 开户银行支行
     */
    private String subBranch;

    /**
     * 开户人
     */
    private String opener;

    /**
     * 创建时间
     */
    @JsonSerialize(using = DateFormatYmtHmsSerializer.class)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 修改时间
     */
    @JsonSerialize(using = DateFormatYmtHmsSerializer.class)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}

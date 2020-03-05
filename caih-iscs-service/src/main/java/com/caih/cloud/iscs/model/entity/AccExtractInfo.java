package com.caih.cloud.iscs.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caih.cloud.iscs.model.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("acc_extract_info")
public class AccExtractInfo extends BaseEntity {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 提现单号
     */
    private String extractNo;

    /**
     * 商户编号
     */
    private String storeNo;

    /**
     * 提现金额
     */
    private BigDecimal extractAmt;

    /**
     * 账户编号
     */
    private String accNo;

    /**
     * 提现状体
     */
    private Integer extractState;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 申请时间
     */
    private Date appDate;

    /**
     * 审核时间
     */
    private Date reviewDate;

    /**
     * 转账时间
     */
    private Date transferDate;

    /**
     * 到账时间
     */
    private Date arriveDate;
}


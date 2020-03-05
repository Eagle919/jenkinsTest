package com.caih.cloud.iscs.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caih.cloud.iscs.model.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.pig.common.util.serializer.DateFormatYmtHmsSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("iscs_acc_extract_info")
public class Extract extends BaseEntity {
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
     * 提现状体 0|申请提现 1|拒绝  2|已通过未转账 3|已转账
     */
    private Integer extractState;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 申请时间
     */
    @JsonSerialize(using = DateFormatYmtHmsSerializer.class)
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date appTime;

    /**
     * 审核时间
     */
    @JsonSerialize(using = DateFormatYmtHmsSerializer.class)
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date reviewTime;

    /**
     * 转账时间
     */
    @JsonSerialize(using = DateFormatYmtHmsSerializer.class)
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date transferTime;

    /**
     * 到账时间
     */
    @JsonSerialize(using = DateFormatYmtHmsSerializer.class)
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date arriveTime;
}

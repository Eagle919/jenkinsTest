package com.caih.cloud.iscs.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caih.cloud.iscs.model.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("iscs_turnover_info")
public class TurnoverInfo extends BaseEntity {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 交易单号
     */
    private String dealNo;

    /**
     * 订单编号/退款订单编号
     */
    private String orderNo;
    /**
     * 流水状态(0:失败;1:成功)
     */
    private String turnoverState;

    /**
     * 商户编号
     */
    private String storeNo;

    /**
     * 流水类型(0:提现;1:收入;2:退款)
     */
    private Integer turnoverType;

    /**
     * 流水金额
     */
    private BigDecimal turnoverAmt;

    /**
     * 账户余额
     */
    private BigDecimal accBalance;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 流水记录创建时间
     */
    private Date turnoverTime;
}

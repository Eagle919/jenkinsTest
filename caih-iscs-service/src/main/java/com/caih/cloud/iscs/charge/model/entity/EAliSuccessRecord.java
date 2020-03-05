package com.caih.cloud.iscs.charge.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.caih.cloud.iscs.model.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 这个表只写 不要修改里面的数据
 */
@Data
@TableName("e_ali_success_record")
public class EAliSuccessRecord extends BaseEntity {

    private String outTradeNo;   // 商户订单号,64个字符以内、只能包含字母、数字、下划线；需保证在商户端不重复

    private String tradeNo;      // 微信订单号

    private String totalAmount;  // 订单总金额，单位元


}

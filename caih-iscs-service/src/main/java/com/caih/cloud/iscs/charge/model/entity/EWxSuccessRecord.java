package com.caih.cloud.iscs.charge.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.caih.cloud.iscs.model.base.BaseEntity;
import lombok.Data;

// 这个表只写 不要修改里面的数据
@Data
@TableName("e_wx_success_record")
public class EWxSuccessRecord extends BaseEntity {

    private String outTradeNo;         // out_trade_no 商户订单号 内部自己生成的

    private String totalFee;           // total_fee    订单总金额 单位为分

    private String transactionId;      // transaction_id 微信支付订单号

}

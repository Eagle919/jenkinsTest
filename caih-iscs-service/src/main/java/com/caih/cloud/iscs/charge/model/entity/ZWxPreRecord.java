package com.caih.cloud.iscs.charge.model.entity;

// 统一下单 https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_1

import com.baomidou.mybatisplus.annotation.TableName;
import com.caih.cloud.iscs.model.base.BaseEntity;
import lombok.Data;

// 这个表只写 后端不读
@Data
@TableName("e_wx_pre_record")
public class ZWxPreRecord extends BaseEntity {

    private String outTradeNo;  // out_trade_no 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*且在同一个商户号下唯一

    private Integer totalFee;   // total_fee    订单总金额 单位为分
}

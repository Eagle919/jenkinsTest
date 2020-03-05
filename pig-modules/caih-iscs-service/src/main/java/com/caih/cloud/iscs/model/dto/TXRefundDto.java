package com.caih.cloud.iscs.model.dto;

import com.caih.cloud.iscs.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("塔西交易退款请求参数")
public class TXRefundDto extends BaseEntity {
    private String storeNo; //商户编号
    private String trxamt; //退款金额（单位：元） 1
    private String reqsn; //商户退款订单号
    private String oldreqsn; //原交易订单号
    private String sign; //签名 1
}

package com.caih.cloud.iscs.model.dto;

import com.caih.cloud.iscs.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("交易退款请求参数")
public class RefundDto extends BaseEntity {
    private String cusid; //商户号1
    private String appid; //应用id 1
    private String version; //版本号(默认填12)
    private String trxamt; //退款金额（单位：分） 1
    private String reqsn; //商户退款订单号 1
    private String oldreqsn; //原交易订单号
    private String oldtrxid; //原交易流水号
//    private String oldxtyjorderno; //原交易小通云缴订单号 1
    private String remark; //备注
    private String randomstr; //随机字符串
    private String signtype; //签名方式（MD5 RSA），默认：MD5
    private String sign; //签名 1
}

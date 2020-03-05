package com.caih.cloud.iscs.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("交易退款响应参数")
public class RefundRespondFail {
    private String retcode;//返回码（SUCCESS/FAIL）
    private String retmsg;//返回码说明
}

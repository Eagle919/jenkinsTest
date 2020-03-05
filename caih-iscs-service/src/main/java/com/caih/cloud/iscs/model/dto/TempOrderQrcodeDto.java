package com.caih.cloud.iscs.model.dto;

import com.caih.cloud.iscs.model.entity.TempOrderQrcode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Kang
 * @date 2019/08/08
 */
@Data
@ApiModel("订单支付二维码信息")
public class TempOrderQrcodeDto extends TempOrderQrcode {
    @ApiModelProperty("订单编号")
    private String orderNo;
    @ApiModelProperty("买家")
    private String buyer;
    @ApiModelProperty("订单总金额")
    private BigDecimal orderTotalAmt;
    @ApiModelProperty("二维码(base64)")
    private String qrCodeStr;
    @ApiModelProperty("通联收银宝MD5key")
    private String md5key;
}
